import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
# Convert startTime to seconds and find the minimum start time for each dataset
file_path_java = '/Users/lin99nn/Documents/cs6650/assignment1/report/client-part2/java-server/server2.csv'
java_data = pd.read_csv(file_path_java)
file_path_go = '/Users/lin99nn/Documents/cs6650/assignment1/report/client-part2/go-server/server2.csv'
go_data = pd.read_csv(file_path_go)

go_data['startTime_sec'] = (go_data['startTime'] - go_data['startTime'].min()) // 1000
java_data['startTime_sec'] = (java_data['startTime'] - java_data['startTime'].min()) // 1000

# Find the test wall time for each dataset
go_walltime = go_data['startTime_sec'].max()
java_walltime = java_data['startTime_sec'].max()

# Create time bins for each second
time_bins_go = np.arange(0, go_walltime + 1, 1)
time_bins_java = np.arange(0, java_walltime + 1, 1)

# Calculate throughput (requests per second) for each server
go_throughput, _ = np.histogram(go_data['startTime_sec'], bins=time_bins_go)
java_throughput, _ = np.histogram(java_data['startTime_sec'], bins=time_bins_java)

# Plotting
plt.figure(figsize=(14, 8))
plt.plot(time_bins_go[:-1], go_throughput, label='Go Server', linewidth=2)
plt.plot(time_bins_java[:-1], java_throughput, label='Java Server', linewidth=2)
plt.xlabel('Time (seconds)')
plt.ylabel('Throughput (requests/second)')
plt.title('Average Throughput Comparison (numThreads = 20)', fontsize=20)
plt.legend()
plt.show()
