'use client';

import * as React from 'react';
import {
  Area,
  Bar,
  Brush,
  CartesianGrid,
  ComposedChart,
  ErrorBar,
  Line,
  LineChart,
  ReferenceLine,
  XAxis,
  YAxis,
} from 'recharts';

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart';
import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'next/navigation';

type DataPoint = {
  date: number;
  totalEnergykWh?: number;
  spaceHeatingkWh?: number;
  waterHeatingkWh?: number;
  electricitykWh?: number;
  totalWaterM3?: number;
  totalEnergyCost?: number;
  buildingHeatingCost?: number;
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
} satisfies ChartConfig;

// Type for metrics
type MetricKey = keyof typeof chartConfig;

export default function Building() {
  const [data, setData] = useState<BuildingData | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeChart, setActiveChart] =
    useState<keyof typeof chartConfig>('electricitykWh');

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

    return allDates.map(date => {
      const estimate = estimateMap[date] ?? null;
      const actual = actualMap[date] ?? null;
      const merged: Record<string, any> = { date: Number(date) };

      (Object.keys(chartConfig) as MetricKey[]).forEach(key => {
        const estimateValue = estimate?.[key] ?? null;
        const actualValue = actual?.[key] ?? null;

        merged[`${key}Estimate`] = estimateValue;
        merged[`${key}Actual`] = actualValue;

        if (estimateValue != null) {
          merged[`${key}EstimateError`] = estimateValue * 0.1;
          merged[`${key}EstimateMinMax`] = [
            estimateValue - estimateValue * 0.1,
            estimateValue + estimateValue * 0.1,
          ];
        }
      });

      return merged;
    });
  }, [data]);

  const estimateColor = '#6ee7b7';
  const actualColor = '#16a34a';

  const unitMap: Record<MetricKey, string> = {
    totalEnergykWh: 'kWh',
    spaceHeatingkWh: 'kWh',
    waterHeatingkWh: 'kWh',
    electricitykWh: 'kWh',
    totalWaterM3: 'm³',
  };

  if (loading) return <div>Laddar byggnad...</div>;

  {
    return (
      <Card>
        <CardHeader className="flex flex-col border-b p-0 sm:items-center sm:justify-between lg:flex-row">
          <div className="flex flex-col gap-1 px-6 py-5 sm:py-6">
            <CardTitle className="text-lg font-semibold sm:text-xl">
              {data?.name}
            </CardTitle>
            <CardDescription className="text-muted-foreground text-sm">
              {chartConfig[activeChart].label +
                ' (' +
                unitMap[activeChart] +
                ')'}
            </CardDescription>
          </div>
          <div className="grid grid-cols-3 px-6 lg:flex">
            {[
              'totalEnergykWh',
              'electricitykWh',
              'spaceHeatingkWh',
              'waterHeatingkWh',
              'totalWaterM3',
            ].map(key => {
              const chart = key as keyof typeof chartConfig;
              return (
                <button
                  key={chart}
                  data-active={activeChart === chart}
                  className="data-[active=true]:bg-muted/50 hover:bg-muted flex flex-col justify-center gap-1 px-6 py-4 text-left transition-colors sm:px-5 sm:py-3"
                  onClick={() => setActiveChart(chart)}
                >
                  <span className="text-muted-foreground text-xs font-medium lg:text-xl">
                    {chartConfig[chart].label}
                  </span>
                </button>
              );
            })}
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
                tickLine={true}
                axisLine={false}
                tickMargin={8}
                tickFormatter={value => {
                  const formatted = new Date(Number(value)).toLocaleDateString(
                    'sv-SE',
                    {
                      month: 'short',
                      year: 'numeric',
                    }
                  );
                  return formatted.charAt(0).toUpperCase() + formatted.slice(1);
                }}
              />
              <YAxis
                tickLine={false}
                axisLine={false}
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
                dot={false}
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
                dot={false}
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
    );
  }
}
