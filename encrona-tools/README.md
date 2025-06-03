# Loggamera Energy Data Collector

This script collects electricity and water consumption data for selected buildings from the Loggamera API and stores the monthly data in a PostgreSQL database.

## Setup

1. **Create and activate a Python virtual environment**

```bash
cd encrona-tools/
python3 -m venv venv
source venv/bin/activate
```

2. **Install dependencies**

```bash
pip install -r requirements.txt
```

## Usage

Run the script:

```bash
python loggamera.py
```

You will be prompted to enter:

- Your Loggamera API key
- A database connection string (e.g. `postgresql://user:password@host:port/dbname`)
- The start month for data collection (e.g. `2024-01`)
- Which buildings you want to process and their associated building IDs in the DB

## Notes

- This script only handles `PowerMeter` and `WaterMeter` devices.
- Data is collected and aggregated per building and per month.
- Existing monthly entries will be updated, not duplicated.
