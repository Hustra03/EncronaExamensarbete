'use client';

import * as React from 'react';
import {
  Brush,
  CartesianGrid,
  ErrorBar,
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
  const [loading, setLoading] = useState(true);
  const [activeChart, setActiveChart] =
    useState<AllMetricKey>('electricitykWh');
  const { id } = useParams();

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

    let currentEstimate = data?.estimate;
    if (!currentEstimate || currentEstimate == undefined) {
      currentEstimate = [];
    }

    setData(prev => ({
      ...prev,
      name: recivedData.building.name,
      installedAt: recivedData.building.installedAt
        ? new Date(recivedData.building.installedAt).getTime()
        : undefined,
      actual: dateToMs(recivedData.actual),
      estimate: [...currentEstimate, ...dateToMs(recivedData.estimate)],
    }));

    setLoading(false);
  }

  /**
   * This function sends a request for new estimates, which will return 200 if some are generated, 204 if no more are needed, or 4** if something went wrong
   */
  async function requestNewEstimates() {
    const res = await fetch('/api/simulationResults/' + id);

    let currentEstimate = data?.estimate;
    if (!currentEstimate || currentEstimate == undefined) {
      currentEstimate = [];
    }

    if (res.status == 200) {
      const recivedData = await res.json();
      if (recivedData != null) {
        setData(prev => ({
          ...prev,
          estimate: [...currentEstimate, ...dateToMs(recivedData.newEstimates)],
        }));
      }
    } else {
      if (!res.ok) {
        //TODO handle errors in some manner here, ex provide a toast to the user
        console.log(res);
      }
    }
  }

  async function refreshEstimates() {
    fetchBuildingCurrentData();
    requestNewEstimates();
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

        if (estimateValue != null) {
          merged[`${key}EstimateError`] = estimateValue * 0.1;
        }
      });

      return merged;
    });
  }, [data]);

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
              <YAxis tickMargin={8} allowDecimals={false} />
              <ChartTooltip
                content={
                  <ChartTooltipContent className="w-[150px]" hideLabel />
                }
              />
              <Line
                dataKey={`${activeChart}Estimate`}
                stroke={estimateColor}
                strokeWidth={2}
                name="Prognos"
                strokeDasharray="4 2"
              >
                <ErrorBar
                  dataKey={`${activeChart}EstimateError`}
                  stroke={estimateColor}
                  direction="y"
                />
              </Line>
              <Line
                dataKey={`${activeChart}Actual`}
                stroke={actualColor}
                strokeWidth={2}
                name="Faktisk"
              />
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
