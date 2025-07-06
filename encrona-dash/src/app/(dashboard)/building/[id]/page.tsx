'use client';

import * as React from 'react';
import {
  Brush,
  CartesianGrid,
  Line,
  LineChart,
  BarChart,
  Bar,
  ReferenceLine,
  XAxis,
  YAxis,
} from 'recharts';

import { Switch } from '@/components/ui/switch';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart';
import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'next/navigation';
import { Button } from '@/components/ui/button';
import Spinner from '@/components/spinner';

type DataPoint = {
  date: number;
  totalEnergykWh?: number;
  spaceHeatingkWh?: number;
  waterHeatingkWh?: number;
  electricitykWh?: number;
  totalWaterM3?: number;
  totalEnergyCost?: number;
  spaceHeatingCost?: number;
  waterHeatingCost?: number;
  electricityCost?: number;
};

type BuildingData = {
  name: string;
  installedAt?: number;
  estimate?: DataPoint[] | null;
  actual?: DataPoint[] | null;
};

const chartConfig = {
  totalEnergykWh: { label: 'Total energi' },
  spaceHeatingkWh: { label: 'Uppvärmning' },
  waterHeatingkWh: { label: 'Tappvarmvatten' },
  electricitykWh: { label: 'El' },
  totalWaterM3: { label: 'Vattenförbrukning' },
};

type EnergyMetricKey = keyof typeof chartConfig;
type AllMetricKey =
  | EnergyMetricKey
  | 'totalEnergyCost'
  | 'spaceHeatingCost'
  | 'waterHeatingCost'
  | 'electricityCost';

export default function Building() {
  const [data, setData] = useState<BuildingData | null>(null);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [benchmarks, setBenchmarks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeChart, setActiveChart] =
    useState<AllMetricKey>('electricitykWh');
  const { id } = useParams();
  const [showActualLine, setShowActualLine] = useState<boolean>(true);
  const [showEstimateLine, setShowEstimateLine] = useState<boolean>(true);
  const [showNormalizedLine, setShowNormalizedLine] = useState<boolean>(false);
  const [showPreInstallationTrendLine, setShowPreInstallationTrendLine] =
    useState<boolean>(false);

  const chartMetricToggleMap: Record<string, AllMetricKey> = {
    electricitykWh: 'electricityCost',
    electricityCost: 'electricitykWh',
    spaceHeatingkWh: 'spaceHeatingCost',
    spaceHeatingCost: 'spaceHeatingkWh',
    waterHeatingkWh: 'waterHeatingCost',
    waterHeatingCost: 'waterHeatingkWh',
    totalEnergykWh: 'totalEnergyCost',
    totalEnergyCost: 'totalEnergykWh',
  };

  const isCost = activeChart.toLowerCase().includes('cost');

  const dateToMs = (arr: DataPoint[]) =>
    arr.map(d => ({ ...d, date: new Date(d.date).getTime() }));

  async function fetchBuildingCurrentData() {
    const res = await fetch('/api/building/' + id);
    const recivedData = await res.json();

    setData(prev => ({
      ...prev,
      name: recivedData.building.name,
      installedAt: recivedData.building.installedAt
        ? new Date(recivedData.building.installedAt).getTime()
        : undefined,
      actual: dateToMs(recivedData.actual),
      estimate: [...(prev?.estimate || []), ...dateToMs(recivedData.estimate)],
    }));
    setLoading(false);
  }

  /**
   * This function sends a request for new estimates, which will return 200 if some are generated, 204 if no more are needed, or 4** if something went wrong
   */
  async function requestNewEstimates() {
    const res = await fetch('/api/simulationResults/' + id);

    if (res.status == 200) {
      const recivedData = await res.json();
      if (recivedData != null) {
        setData(prev => ({
          ...prev,
          estimate: [
            ...(prev?.estimate || []),
            ...dateToMs(recivedData.newEstimates),
          ],
        }));
      }
    } else {
      if (!res.ok) {
        //TODO handle errors in some manner here, ex provide a toast to the user
        console.log(res);
      }
    }
  }

  async function fetchBenchmarks() {
    const res = await fetch(`/api/benchmarkData?buildingId=${id}`);
    if (res.ok) {
      const data = await res.json();
      setBenchmarks(data);
    }
  }

  async function refreshEstimates() {
    fetchBuildingCurrentData();
    requestNewEstimates();
    fetchBenchmarks();
  }

  useEffect(() => {
    refreshEstimates();
  }, [id]);

  const chartData = useMemo(() => {
    if (!data) return [];

    const estimateMap = Object.fromEntries(
      (data.estimate ?? []).map(entry => [entry.date, entry])
    );
    const actualMap = Object.fromEntries(
      (data.actual ?? []).map(entry => [entry.date, entry])
    );

    const allDates = Array.from(
      new Set([...Object.keys(estimateMap), ...Object.keys(actualMap)])
    ).sort((a, b) => Number(a) - Number(b));

    const allMetrics: AllMetricKey[] = [
      'totalEnergykWh',
      'spaceHeatingkWh',
      'waterHeatingkWh',
      'electricitykWh',
      'totalWaterM3',
      'totalEnergyCost',
      'spaceHeatingCost',
      'waterHeatingCost',
      'electricityCost',
    ];

    const benchmarkMap = new Map<string, Record<string, number>>();

    benchmarks.forEach(b => {
      const key = `${b.type}-${b.energyType}`;
      benchmarkMap.set(key, b);
    });

    const months = [
      'january',
      'february',
      'march',
      'april',
      'may',
      'june',
      'july',
      'august',
      'september',
      'october',
      'november',
      'december',
    ];

    return allDates.map(date => {
      const estimate = estimateMap[date] ?? null;
      const actual = actualMap[date] ?? null;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const merged: Record<string, any> = { date: Number(date) };

      allMetrics.forEach(key => {
        const estimateValue = estimate?.[key] ?? null;
        const actualValue = actual?.[key] ?? null;

        const diff =
          actualValue != null && estimateValue != null
            ? estimateValue - actualValue
            : null;

        const relativeError =
          actualValue != null && estimateValue != null && actualValue !== 0
            ? (estimateValue - actualValue) / actualValue
            : null;

        merged[`${key}Estimate`] = estimateValue;
        merged[`${key}Actual`] = actualValue;
        merged[`${key}Diff`] = diff;
        merged[`${key}RelativeError`] = relativeError;

        const energyType = key
          .replace('totalEnergy', 'TOTAL')
          .replace('spaceHeating', 'SPACE_HEATING')
          .replace('waterHeating', 'WATER_HEATING')
          .replace('electricity', 'ELECTRICITY')
          .replace('totalWaterM3', 'WATER_USAGE')
          .replace(/(kWh|Cost)$/, '');

        const dateObj = new Date(Number(date));
        const monthIndex = dateObj.getUTCMonth();
        const monthKey = months[monthIndex];

        const normalized = benchmarkMap.get(`NORMALIZED-${energyType}`);
        const preAction = benchmarkMap.get(`PRE_ACTION-${energyType}`);

        if (normalized && monthKey in normalized) {
          merged[`${key}Normalized`] = normalized[monthKey];
        }

        if (preAction && monthKey in preAction) {
          merged[`${key}PreAction`] = preAction[monthKey];
        }
      });

      return merged;
    });
  }, [data, benchmarks]);

  const estimateColor = '#6ee7b7';
  const actualColor = '#16a34a';

  const unitMap: Record<AllMetricKey, string> = {
    totalEnergykWh: 'kWh',
    spaceHeatingkWh: 'kWh',
    waterHeatingkWh: 'kWh',
    electricitykWh: 'kWh',
    totalWaterM3: 'm³',
    totalEnergyCost: 'kr',
    spaceHeatingCost: 'kr',
    waterHeatingCost: 'kr',
    electricityCost: 'kr',
  };

  if (loading) return <Spinner />;

  return (
    <>
      <Card>
        <CardHeader className="flex flex-col border-b p-0 sm:items-center sm:justify-between lg:flex-row">
          <div className="flex flex-col gap-1 px-6 py-5 sm:py-6">
            <CardTitle className="text-lg font-semibold sm:text-xl">
              {data?.name}
            </CardTitle>
            <CardDescription className="text-muted-foreground flex items-center gap-2 text-sm">
              {chartMetricToggleMap[activeChart] && (
                <>
                  <span className="text-xs">Visa kostnad</span>
                  <Switch
                    checked={isCost}
                    onCheckedChange={() =>
                      setActiveChart(
                        chartMetricToggleMap[activeChart] as AllMetricKey
                      )
                    }
                  />
                </>
              )}
            </CardDescription>
            <Button onClick={refreshEstimates}>Förnya skattningar</Button>
          </div>
          <div className="flex flex-col gap-1 px-6 py-5 sm:py-6">
            <span className="text-xs">Visa verkliga linjen</span>
            <Switch
              checked={showActualLine}
              onCheckedChange={() => setShowActualLine(!showActualLine)}
            />

            <span className="text-xs">Visa skattningslinjen</span>
            <Switch
              checked={showEstimateLine}
              onCheckedChange={() => setShowEstimateLine(!showEstimateLine)}
            />

            <span className="text-xs">Visa normaliserad linje</span>
            <Switch
              checked={showNormalizedLine}
              onCheckedChange={() => setShowNormalizedLine(!showNormalizedLine)}
            />

            <span className="text-xs">Visa trend-linje</span>
            <Switch
              checked={showPreInstallationTrendLine}
              onCheckedChange={() =>
                setShowPreInstallationTrendLine(!showPreInstallationTrendLine)
              }
            />
          </div>

          <div className="grid grid-cols-3 px-6 lg:flex">
            {Object.keys(chartConfig).map(key => (
              <button
                key={key}
                data-active={activeChart === key}
                className="data-[active=true]:bg-muted/50 hover:bg-muted flex flex-col justify-center gap-1 px-6 py-4 text-left transition-colors sm:px-5 sm:py-3"
                onClick={() => setActiveChart(key as AllMetricKey)}
              >
                <span className="text-muted-foreground text-xs font-medium lg:text-xl">
                  {chartConfig[key as keyof typeof chartConfig].label}
                </span>
              </button>
            ))}
          </div>
        </CardHeader>

        <CardContent className="px-2 sm:p-6">
          <ChartContainer
            config={chartConfig}
            className="aspect-auto h-[250px] w-full"
          >
            <LineChart accessibilityLayer data={chartData} syncId="default">
              <CartesianGrid vertical={false} />
              <XAxis
                dataKey="date"
                type="number"
                scale="time"
                domain={['auto', 'auto']}
                tickMargin={8}
                tickFormatter={value =>
                  new Date(Number(value)).toLocaleDateString('sv-SE', {
                    month: 'short',
                    year: 'numeric',
                  })
                }
              />
              <YAxis
                tickMargin={8}
                allowDecimals={false}
                unit={unitMap[activeChart]}
                tickFormatter={value => {
                  if (Math.abs(value) >= 1_000_000)
                    return `${(value / 1_000_000).toFixed(1)}M `;
                  if (Math.abs(value) >= 1_000)
                    return `${(value / 1_000).toFixed(1)}k `;
                  return value;
                }}
                padding={{ top: 10 }}
              />
              <ChartTooltip
                content={
                  <ChartTooltipContent className="w-[150px]" hideLabel />
                }
              />
              {showEstimateLine && (
                <Line
                  dataKey={`${activeChart}Estimate`}
                  stroke={estimateColor}
                  strokeWidth={2}
                  name="Prognos"
                  strokeDasharray="4 2"
                ></Line>
              )}

              {showActualLine && (
                <Line
                  dataKey={`${activeChart}Actual`}
                  stroke={actualColor}
                  strokeWidth={2}
                  name="Faktisk"
                />
              )}

              {showNormalizedLine && (
                <Line
                  dataKey={`${activeChart}Normalized`}
                  stroke="#3b82f6"
                  strokeWidth={2}
                  name="Normalår"
                  strokeDasharray="2 2"
                />
              )}

              {showPreInstallationTrendLine && (
                <Line
                  dataKey={`${activeChart}PreAction`}
                  stroke="#facc15"
                  strokeWidth={2}
                  name="Före åtgärd"
                  strokeDasharray="2 2"
                />
              )}

              {data?.installedAt && (
                <ReferenceLine
                  x={data.installedAt}
                  stroke="#dc2626"
                  label={{
                    value: 'Installation',
                    position: 'right',
                    offset: 10,
                    fill: '#dc2626',
                    fontSize: 12,
                  }}
                  isFront
                />
              )}
              <Brush />
            </LineChart>
          </ChartContainer>
        </CardContent>
      </Card>

      <Card className="mt-8">
        <CardHeader className="flex flex-col border-b p-0 sm:items-center sm:justify-between lg:flex-row">
          <div className="flex flex-col gap-1 px-6 py-5 sm:py-6">
            <CardTitle className="text-lg font-semibold sm:text-xl">
              Skillnad mellan prognos och faktiskt värde
            </CardTitle>
          </div>
        </CardHeader>
        <CardContent className="px-2 sm:p-6">
          <ChartContainer
            config={chartConfig}
            className="aspect-auto h-[200px] w-full"
          >
            <BarChart data={chartData} syncId="default">
              <CartesianGrid vertical={false} />
              <XAxis
                dataKey="date"
                type="number"
                scale="time"
                domain={['auto', 'auto']}
                tickMargin={8}
                tickFormatter={value =>
                  new Date(Number(value)).toLocaleDateString('sv-SE', {
                    month: 'short',
                    year: 'numeric',
                  })
                }
              />
              <YAxis tickMargin={8} />
              <ChartTooltip
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    const diff = payload[0].payload[`${activeChart}Diff`];
                    const relative =
                      payload[0].payload[`${activeChart}RelativeError`];
                    return (
                      <div className="bg-background rounded-md border p-2 shadow-sm">
                        <div>
                          Skillnad: {diff?.toFixed(1)} {unitMap[activeChart]}
                        </div>
                        {relative != null && (
                          <div>
                            Relativt fel: {(relative * 100).toFixed(1)}%
                          </div>
                        )}
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Bar dataKey={`${activeChart}Diff`} fill="#60a5fa" />
            </BarChart>
          </ChartContainer>
        </CardContent>
      </Card>
    </>
  );
}
