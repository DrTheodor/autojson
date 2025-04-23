---
title: Benchmark
---

Benchmarked on [`18d5e30`](https://github.com/DrTheodor/autojson/commit/18d5e302f23f07b8b1070451f15ef67f0c52a609).

## My Laptop

> **Ran on**: Microsoft Surface Laptop
>
> **CPU**: Intel i5-7300U
>
> **RAM**: 8GB
>
> **OS**: Windows 10 Enterprise LTSC

| Benchmark                             | Mode | Cnt |      Score |       Error | Units |
|---------------------------------------|:----:|:---:|-----------:|------------:|-------|
| ToJsonBenchmarks.autoSerialize        | avgt | 10  |  11340.972 |   ± 483.899 | ns/op |
| ToJsonBenchmarks.gsonSerialize        | avgt | 10  |  41952.507 |   ± 564.717 | ns/op |
| ToObjBenchmarks.autoDeserialize       | avgt | 10  |   7022.748 |    ± 94.389 | ns/op |
| ToObjBenchmarks.gsonDeserialize       | avgt | 10  |  34820.648 |  ± 1616.649 | ns/op |
| ait.ToJsonAITBenchmark.autoSerialize  | avgt | 10  |  46915.092 |  ± 1791.029 | ns/op |
| ait.ToJsonAITBenchmark.gsonSerialize  | avgt | 10  | 206509.310 | ± 19026.570 | ns/op |
| ait.ToObjAITBenchmark.autoDeserialize | avgt | 10  |  80841.016 |  ± 5698.545 | ns/op |
| ait.ToObjAITBenchmark.gsonDeserialize | avgt | 10  | 236694.069 | ± 13047.773 | ns/op |

![Performance comparison bar chart taken on my laptop](../../assets/performance_laptop.png)

## GitHub Actions

> **Ran on**: GitHub Actions
> 
> **OS**: ubuntu-latest

| Benchmark                             | Mode | Cnt |      Score |     Error | Units |
|---------------------------------------|:----:|:---:|-----------:|----------:|-------|
| ToJsonBenchmarks.autoSerialize        | avgt | 10  |   7824.706 |  ± 33.084 | ns/op |
| ToJsonBenchmarks.gsonSerialize        | avgt | 10  |  30148.625 |  ± 96.032 | ns/op |
| ToObjBenchmarks.autoDeserialize       | avgt | 10  |   4770.734 |  ± 13.164 | ns/op |
| ToObjBenchmarks.gsonDeserialize       | avgt | 10  |  24686.506 |  ± 60.086 | ns/op |
| ait.ToJsonAITBenchmark.autoSerialize  | avgt | 10  |  21606.580 |  ± 47.853 | ns/op |
| ait.ToJsonAITBenchmark.gsonSerialize  | avgt | 10  | 116371.066 | ± 198.473 | ns/op |
| ait.ToObjAITBenchmark.autoDeserialize | avgt | 10  |  36745.185 |  ± 91.605 | ns/op |
| ait.ToObjAITBenchmark.gsonDeserialize | avgt | 10  | 124342.297 | ± 356.169 | ns/op |

![Performance comparison bar chart taken with GitHub Actions](../../assets/performance_actions.png)