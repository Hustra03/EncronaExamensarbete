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
  owner: string;
  installedAt?: number;
  estimate?: DataPoint[] | null;
  actual?: DataPoint[] | null;
};

const chartConfig = {
  totalEnergykWh: {
    label: 'Total energi',
  },
  spaceHeatingkWh: {
    label: 'Uppvärmning',
  },
  waterHeatingkWh: {
    label: 'Tappvarmvatten',
  },
  electricitykWh: {
    label: 'El',
  },
  totalWaterM3: {
    label: 'Vattenförbrukning',
  },
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
  const { id } = useParams();

  useEffect(() => {
    const source = new EventSource(`/api/building/${id}`);

    const dateToMs = (arr: DataPoint[]) =>
      arr.map(d => ({ ...d, date: new Date(d.date).getTime() }));

    source.addEventListener('actual', e => {
      const parsed = JSON.parse(e.data);
      setData(prev => ({
        ...prev,
        name: parsed.building.name,
        owner: parsed.building.owner,
        installedAt: parsed.building.installedAt
          ? new Date(parsed.building.installedAt).getTime()
          : undefined,
        actual: dateToMs(parsed.data),
      }));
      setLoading(false);
    });

    source.addEventListener('estimate', e => {
      const parsed = JSON.parse(e.data);
      setData(prev => ({
        ...(prev as BuildingData),
        estimate: dateToMs(parsed),
      }));
    });

    return () => {
      source.close();
    };
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

        const absDiff =
          actualValue != null && estimateValue != null
            ? Math.abs(actualValue - estimateValue)
            : null;

        merged[`${key}Estimate`] = estimateValue;
        merged[`${key}Actual`] = actualValue;
        merged[`${key}Diff`] = absDiff;

        if (estimateValue != null) {
          merged[`${key}EstimateError`] = estimateValue * 0.1;
        }
      });

      return merged;
    });
  }, [data]);

  async function requestNewEstimates()
  {
    const result=await fetch(`/api/simulationResults/${id}`);
    console.log(result);

  }

  const estimateColor = '#6ee7b7';
  const actualColor = '#16a34a';

  if (loading) return <div>Laddar byggnad...</div>;

  return (
    <>
      <Card>
        <CardHeader className="flex flex-col border-b p-0 sm:items-center sm:justify-between lg:flex-row">
          <div className="flex flex-col gap-1 px-6 py-5 sm:py-6">
            <CardTitle className="text-lg font-semibold sm:text-xl">
              {data?.name}
            </CardTitle>
            <CardDescription className="text-muted-foreground text-sm">
              {chartMetricToggleMap[activeChart] && (
                <div className="flex items-center gap-2">
                  <span className="text-xs">Visa kostnad</span>
                  <Switch
                    checked={isCost}
                    onCheckedChange={() =>
                      setActiveChart(
                        chartMetricToggleMap[activeChart] as AllMetricKey
                      )
                    }
                  />
                </div>
              )}
            </CardDescription>
            <Button onClick={requestNewEstimates}>Flera skattningar</Button>
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
            <LineChart accessibilityLayer data={chartData}>
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
                domain={([min, max]) => [Math.floor(min * 0.9), max]}
                allowDecimals={false}
              />
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
                  isFront={true}
                />
              )}
              <Brush />
            </LineChart>
          </ChartContainer>
        </CardContent>
      </Card>

      <Card className="mt-8">
        <CardContent className="px-2 sm:p-6">
          <h4 className="text-muted-foreground mb-2 text-sm font-medium">
            Absolut skillnad mellan prognos och faktiskt värde
          </h4>
          <ChartContainer
            config={chartConfig}
            className="aspect-auto h-[200px] w-full"
          >
            <BarChart data={chartData}>
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
                content={
                  <ChartTooltipContent
                    className="w-[160px]"
                    hideLabel={false}
                  />
                }
              />
              <Bar
                dataKey={`${activeChart}Diff`}
                fill="#60a5fa"
                name="Absolut skillnad"
              />
            </BarChart>
          </ChartContainer>
        </CardContent>
      </Card>
    </>
  );
}
