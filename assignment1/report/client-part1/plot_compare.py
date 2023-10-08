import matplotlib.pyplot as plt
import numpy as np

# Data for plotting
num_thread_groups = np.array([10, 20, 30])

# Throughput data for Go Server
throughput_go = np.array([2025.509385528638, 2519.0810993721097, 2772.5836611000063])

# Throughput data for Java Server
throughput_java = np.array([2055.519374796483, 2495.096700513915, 2664.117610613986])

# Plotting
plt.figure(figsize=(10, 6))
plt.plot(num_thread_groups, throughput_java, label='Java Server')
plt.plot(num_thread_groups, throughput_go, label='Go Server')

# Labels and title
plt.xlabel('Number of Thread Groups')
plt.ylabel('Throughput (req/s)')
plt.title('Throughput Comparison between Go and Java Servers', fontsize=18)
plt.xticks(num_thread_groups)
plt.legend()

plt.show()
