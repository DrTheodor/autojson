---
title: Benchmark
---

Benchmarked on [`5825bce`](https://github.com/DrTheodor/autojson/commit/5825bce3f9eb2b385f5f79baff7e7c17a99c939d).

# My Laptop

> Microsoft Surface Laptop
> **CPU**: Intel i5-7300U
> **RAM**: 8GB
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

![Performance comparison bar chart](../../assets/performance_laptop.png)

# GitHub Actions

> Ran on GitHub Actions
> **OS**: ubuntu-latest



![Performance comparison bar chart](../../assets/performance_laptop.png)