import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# Load the data
file_path_go = '/Users/lin99nn/Documents/cs6650/assignment1/report/client-part2/go-server/server1.csv'
go_data = pd.read_csv(file_path_go)

# Convert startTime to seconds and align it
go_data['startTime_sec'] = (go_data['startTime'] - go_data['startTime'].min()) // 1000

# Find the test wall time
go_walltime = go_data['startTime_sec'].max()

# Create time bins for each second
time_bins_go = np.arange(0, go_walltime + 1, 1)

# Calculate throughput (requests per second)
go_throughput, _ = np.histogram(go_data['startTime_sec'], bins=time_bins_go)

# Plotting
plt.figure(figsize=(14, 8))
plt.plot(time_bins_go[:-1], go_throughput, label='Go Server', linewidth=2)
plt.xlabel('Time (seconds)')
plt.ylabel('Throughput (requests/second)')
plt.title('Average Throughput for Go Server')
plt.legend()
plt.show()
