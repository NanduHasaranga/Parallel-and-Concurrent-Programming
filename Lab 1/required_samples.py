import math

def required_samples(mean, std, accuracy=0.05, confidence=0.95):
    # Z value for 95% confidence is 1.96
    Z = 1.96 if confidence == 0.95 else 2.58 if confidence == 0.99 else 1.64
    N = (Z * std / (accuracy * mean)) ** 2
    return math.ceil(N)

if __name__ == "__main__":
    # Example usage: replace with your mean and std
    mean = 0.007500  # average execution time (seconds)
    std = 0.000500   # standard deviation (seconds)
    samples = required_samples(mean, std)
    print(f"Required samples for ±5% accuracy and 95% confidence: {samples}")
    # Or prompt user for input:
    # mean = float(input("Enter mean: "))
    # std = float(input("Enter std deviation: "))
    # print(f"Required samples: {required_samples(mean, std)}")
