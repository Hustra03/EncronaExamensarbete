import requests
import asyncio
from datetime import datetime, timedelta, timezone
from dateutil.relativedelta import relativedelta
from sqlmodel import SQLModel, Field, create_engine, Session, select
from sqlalchemy import Column, DateTime, func
import os

API_KEY = ""
DB_URL = ""
HEADERS = {"Content-Type": "application/json"}
BASE_URL = "https://platform.loggamera.se/api/v2"
ROOT_PARENT_ID = 9647 # Encrona organisation ID root för allt
EARLIEST_DATE = "2020-01-01T00:00:00Z" # Datum som garanterat är före någon installation utförts

# ---------------- Models ---------------- #

class BuildingData(SQLModel, table=True):
    __tablename__ = "BuildingData"
    id: int | None = Field(default=None, primary_key=True)
    buildingId: int
    type: str
    date: datetime
    electricitykWh: float | None = None
    totalWaterM3: float | None = None
    createdAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    updatedAt: datetime = Field(
        sa_column=Column(DateTime(timezone=True), onupdate=func.now(), default=func.now())
    )

# ---------------- DB Setup ---------------- #

engine = create_engine(DB_URL)
SQLModel.metadata.create_all(engine)

# ---------------- Loggamera API ---------------- #

def post(endpoint, payload):
    response = requests.post(f"{BASE_URL}/{endpoint}", headers=HEADERS, json=payload)
    response.raise_for_status()
    return response.json()["Data"]

def get_organizations():
    return post("Organizations", {"ApiKey": API_KEY})["Organizations"]

def get_children(orgs, parent_id):
    return [org for org in orgs if org["ParentId"] == parent_id]

def get_all_sub_organizations(orgs, parent_id):
    result = [parent_id]
    for child in get_children(orgs, parent_id):
        result.extend(get_all_sub_organizations(orgs, child["Id"]))
    return result

def get_devices(org_id):
    return post("Devices", {"ApiKey": API_KEY, "OrganizationId": org_id})["Devices"]

def get_installation_date(class_name, device_id):
    endpoint = "PowerMeter" if class_name == "PowerMeter" else "WaterMeter"
    payload = {"ApiKey": API_KEY, "DeviceId": device_id, "DateTimeUtc": EARLIEST_DATE}
    result = post(endpoint, payload)
    return datetime.fromisoformat(result["LogDateTimeUtc"].replace("Z", ""))

def get_total_consumed(class_name, device_id, date):
    endpoint = "PowerMeter" if class_name == "PowerMeter" else "WaterMeter"
    payload = {
        "ApiKey": API_KEY,
        "DeviceId": device_id,
        "DateTimeUtc": date.strftime("%Y-%m-%dT%H:%M:%SZ")
    }
    result = post(endpoint, payload)
    if not result or "Values" not in result:
        return 0

    expected_name = "ConsumedTotalInkWh" if class_name == "PowerMeter" else "ConsumedTotalInM3"

    for val in result["Values"]:
        if val["Name"] == expected_name:
            try:
                return float(val["Value"])
            except (ValueError, TypeError):
                return 0
    return 0

def calculate_monthly_deltas(class_name, device_id, months):
    monthly_data = []
    previous_value = get_total_consumed(class_name, device_id, months[0] - timedelta(seconds=1))
    now = datetime.now(timezone.utc)

    for i, month_start in enumerate(months):
        if i + 1 < len(months):
            period_end = months[i + 1] - timedelta(seconds=1)
        else:
            period_end = now.replace(hour=0, minute=0, second=0, microsecond=0) - timedelta(seconds=1)

        current_value = get_total_consumed(class_name, device_id, period_end)
        delta = current_value - previous_value
        monthly_data.append((month_start.strftime("%Y-%m"), delta))
        previous_value = current_value

    return monthly_data

# ---------------- CLI Interaction ---------------- #

def prompt_user_selection(child_orgs):
    print("\nVälj byggnader att hämta data från (t.ex. 1,3,5):")
    for idx, org in enumerate(child_orgs, start=1):
        print(f"{idx}. {org['Name']} (ID: {org['Id']})")
    choice = input("\nSkriv siffrorna för valda byggnader: ").strip()
    indexes = [int(i) for i in choice.split(',') if i.strip().isdigit()]
    return [child_orgs[i - 1] for i in indexes if 1 <= i <= len(child_orgs)]

# ---------------- DB Operations ---------------- #

def building_data_exists(building_id):
    with Session(engine) as session:
        stmt = select(BuildingData).where(BuildingData.buildingId == building_id, BuildingData.type == "ACTUAL")
        return session.exec(stmt).first() is not None

def submit_to_sqlmodel(building_id, data_per_month):
    with Session(engine) as session:
        for month_str, values in data_per_month.items():
            date = datetime.strptime(month_str, "%Y-%m").replace(tzinfo=timezone.utc)
            existing = session.exec(
                select(BuildingData).where(
                    BuildingData.buildingId == building_id,
                    BuildingData.type == "ACTUAL",
                    BuildingData.date == date
                )
            ).first()

            if existing:
                existing.electricitykWh = values["kWh"]
                existing.totalWaterM3 = values["m³"]
            else:
                session.add(BuildingData(
                    buildingId=building_id,
                    type="ACTUAL",
                    date=date,
                    electricitykWh=values["kWh"],
                    totalWaterM3=values["m³"],
                ))
        session.commit()

# ---------------- Main Logic ---------------- #

async def process_building(orgs, building_org, building_id):
    print(f"\nBearbetar: {building_org['Name']} (ID: {building_org['Id']})")

    all_sub_org_ids = get_all_sub_organizations(orgs, building_org["Id"])

    install_date = None
    for org_id in all_sub_org_ids:
        devices = get_devices(org_id)
        for d in devices:
            if d["Class"] in ["PowerMeter", "WaterMeter"]:
                install_date = get_installation_date(d["Class"], d["Id"])
                break
        if install_date:
            break

    if not install_date:
        print("Inga mätare hittades i byggnaden.")
        return

    install_date = install_date.replace(day=1, hour=0, minute=0, second=0, microsecond=0, tzinfo=timezone.utc)
    now_month = datetime.now(timezone.utc).replace(day=1, hour=0, minute=0, second=0, microsecond=0)
    months = []
    current = install_date
    while current <= now_month:
        months.append(current)
        current += relativedelta(months=1)

    if building_data_exists(building_id):
        months = [months[-1]]

    total_consumption = [{"kWh": 0, "m³": 0} for _ in months]

    for org_id in all_sub_org_ids:
        devices = get_devices(org_id)
        for d in devices:
            if d["Class"] not in ["PowerMeter", "WaterMeter"]:
                continue
            data = calculate_monthly_deltas(d["Class"], d["Id"], months)
            unit = "kWh" if d["Class"] == "PowerMeter" else "m³"
            for i, (_, value) in enumerate(data):
                total_consumption[i][unit] += value

    data_per_month = {}
    for i, month in enumerate(months):
        kwh = total_consumption[i]["kWh"]
        m3 = total_consumption[i]["m³"]
        print(f"{month.strftime('%Y-%m')}:  El: {kwh:.2f} kWh   Vatten: {m3:.2f} m³")
        data_per_month[month.strftime("%Y-%m")] = {"kWh": kwh, "m³": m3}

    submit_to_sqlmodel(building_id, data_per_month)

# ---------------- Entry Point ---------------- #

async def main():
    global API_KEY
    API_KEY = input("Ange din API-nyckel: ").strip()
    all_orgs = get_organizations()
    top_level = get_children(all_orgs, ROOT_PARENT_ID)

    if not top_level:
        print("Inga byggnader hittades.")
        return

    selected = prompt_user_selection(top_level)

    if not selected:
        print("Inga byggnader valda.")
        return

    for building in selected:
        db_id = int(input(f"Ange för '{building['Name']}': ").strip())
        await process_building(all_orgs, building, db_id)

if __name__ == "__main__":
    asyncio.run(main())
