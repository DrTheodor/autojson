import matplotlib.pyplot as plt
import numpy as np

def parse_benchmark_file(filename):
    """Parse either format by first stripping all pipes"""
    with open(filename) as f:
        lines = [line.replace('|', ' ') for line in f if line.strip()]

    data = []
    for line in lines[1:]:
        parts = [p for p in line.split()]
        if len(parts) >= 6:
            benchmark = parts[0]
            score = float(parts[3])
            error = float(parts[5])
            impl = 'auto' if 'auto' in benchmark.lower() else 'gson'
            splitat = benchmark.rindex('.')
            className, methodName = benchmark[:splitat], benchmark[splitat + 1:]
            className = className.replace('Benchmark', '')
            methodName = methodName.replace(impl, '')

            data.append((f'{className} ({methodName})', impl, score, error))
    return data

def plot_benchmarks(data):
    # Group by operation (preserving order)
    ops = []
    auto_vals, gson_vals = [], []
    auto_errs, gson_errs = [], []

    # Pair auto/gson entries
    for i in range(0, len(data), 2):
        op = data[i][0].replace('auto', '').replace('gson', '').strip()
        ops.append(op)
        auto_vals.append(data[i][2])
        auto_errs.append(data[i][3])
        gson_vals.append(data[i+1][2])
        gson_errs.append(data[i+1][3])

    # Plotting
    x = np.arange(len(ops))
    width = 0.35

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.bar(x - width/2, auto_vals, width, yerr=auto_errs,
           label='Auto', capsize=3, color='skyblue')
    ax.bar(x + width/2, gson_vals, width, yerr=gson_errs,
           label='Gson', capsize=3, color='salmon')

    auto_total = [v + e for v, e in zip(auto_vals, auto_errs)]
    gson_total = [v + e for v, e in zip(gson_vals, gson_errs)]

    for i, (val, err, total) in enumerate(zip(auto_vals, auto_errs, auto_total)):
        ax.text(i - width/2, total, f'{val:.1f} ±{err:.1f}',
                ha='center', va='bottom', fontsize=9)

    # For Gson bars
    for i, (val, err, total) in enumerate(zip(gson_vals, auto_errs, gson_total)):
        ax.text(i + width/2, total, f'{val:.1f} ±{err:.1f}',
                ha='center', va='bottom', fontsize=9)

    ax.set_title('Serialization Performance Comparison')
    ax.set_ylabel('Time (ms/op)')
    ax.set_xticks(x)
    ax.set_xticklabels(ops, rotation=45, ha='right')
    ax.legend()
    ax.grid(axis='y', linestyle='--', alpha=0.7)

    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    import sys
    data = parse_benchmark_file(sys.argv[1] if len(sys.argv) > 1 else 'build/results/jmh/results.txt')
    plot_benchmarks(data)