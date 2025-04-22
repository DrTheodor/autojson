import re
import sys

def convert_to_markdown(text):
    lines = [line.strip() for line in text.strip().split('\n') if line.strip()]

    # Extract headers
    headers = re.split(r'\s{2,}', lines[0])
    headers = [h.strip() for h in headers if h.strip()]

    # Process data rows
    table_data = []
    for line in lines[1:]:
        # Remove any trailing pipes
        line = line.rstrip('|').strip()

        # Split on whitespace
        parts = re.split(r'\s+', line)

        # Find where metrics start (after benchmark name)
        # The benchmark name ends right before 'avgt'
        mode_index = parts.index('avgt')
        benchmark_name = ' '.join(parts[:mode_index])

        # Extract metrics
        mode = parts[mode_index]
        cnt = parts[mode_index+1]
        score = parts[mode_index+2]
        error = f"± {parts[mode_index+4]}"  # parts[mode_index+3] is '±'
        units = parts[mode_index+5]

        table_data.append([benchmark_name, mode, cnt, score, error, units])

    # Generate markdown table
    md_lines = [
        f"| {' | '.join(headers)} |",
        f"| {' | '.join(':---:' if h in ['Mode', 'Cnt'] else '---:' if h in ['Score', 'Error'] else '---' for h in headers)} |"
    ]

    for row in table_data:
        md_lines.append(f"| {' | '.join(row)} |")

    return '\n'.join(md_lines)


input_text = None
with open(sys.argv[1] if len(sys.argv) > 1 else 'build/results/jmh/results.txt', 'r', encoding='utf-8') as f:
    input_text = f.read()

print(convert_to_markdown(input_text))