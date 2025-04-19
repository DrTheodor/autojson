---
title: Benchmark
---

| Benchmark                    | Mode | Cnt |     Score | Error     | Units |
|------------------------------|:----:|:---:|----------:|-----------|:-----:|
| AITBenchmark.autoSerialize   | avgt |  10 |  3467.611 | ± 211.629 | ms/op |
| AITBenchmark.gsonSerialize   | avgt |  10 | 31903.850 | ± 79.404  | ms/op |
| ToJsonBenchmarks.autoObj2Str | avgt |  10 |  6359.892 | ± 12.770  | ms/op |
| ToJsonBenchmarks.gsonObj2Str | avgt |  10 | 11846.072 | ± 103.042 | ms/op |
| ToObjBenchmarks.autoStr2Obj  | avgt |  10 |  3220.653 | ± 5.248   | ms/op |
| ToObjBenchmarks.gsonStr2Obj  | avgt |  10 |  4729.724 | ± 63.249  | ms/op |

(lower score is better)
Benchmarked on `16.04.25`.
