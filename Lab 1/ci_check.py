import math

def check_confidence(times):
    N = len(times)
    mean = sum(times) / N
    std = math.sqrt(sum((t - mean) ** 2 for t in times) / N)
    ci = 1.96 * (std / math.sqrt(N))
    print(f"Mean: {mean:.6f}")
    print(f"Std: {std:.6f}")
    print(f"95% Confidence Interval: ±{ci:.6f}")
    print(f"5% of Mean: {0.05 * mean:.6f}")
    if ci < 0.05 * mean:
        print("Your results meet the ±5% accuracy and 95% confidence level.")
    else:
        print("Increase the number of runs (N) for tighter confidence interval.")

if __name__ == "__main__":
    # Example usage: paste your times here
    times = [0.002900, 0.002900, 0.003000, 0.002900, 0.003000]  # Replace with your actual timings
    check_confidence(times)
    # Or read from a file:
    # with open('times.txt') as f:
    #     times = [float(line.strip()) for line in f]
    # check_confidence(times)
