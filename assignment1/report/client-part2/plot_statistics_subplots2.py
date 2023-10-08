import matplotlib.pyplot as plt
java_metrics_new = [
    {'numThreadGroups': 10, 'metrics': [55416, 1822.6440971595625, 25.977821782178218, 28.27487128712871, 24.0, 27.0, 49.0, 53.0, 10.0, 12.0, 256.0, 349.0]},
    {'numThreadGroups': 20, 'metrics': [75979, 2645.5374652855467, 35.91497512437811, 38.637149253731344, 35.0, 37.0, 66.0, 73.0, 11.0, 12.0, 347.0, 309.0]},
    {'numThreadGroups': 30, 'metrics': [122615, 2454.8783571073213, 57.179109634551494, 60.570394413363275, 46.0, 48.0, 242.0, 275.0, 11.0, 13.0, 8297.0, 8297.0]}
]

go_metrics_new = [
    {'numThreadGroups': 10, 'metrics': [46001, 2195.699906519707, 21.54753465346535, 23.83568316831683, 20.0, 22.0, 48.0, 51.0, 10.0, 12.0, 246.0, 255.0]},
    {'numThreadGroups': 20, 'metrics': [72978, 2754.330190747643, 34.89301492537314, 37.11939303482587, 34.0, 36.0, 64.0, 68.0, 10.0, 12.0, 299.0, 309.0]},
    {'numThreadGroups': 30, 'metrics': [118795, 2533.819332788969, 56.59259468438538, 59.431714285714285, 47.0, 48.0, 522.0, 525.0, 11.0, 12.0, 1180.0, 1168.0]}
]

labels = ['Time taken (ms)', 'Total throughput (req/s)', 
          'Mean response time GET (ms)', 'Mean response time POST (ms)',
          'Median response time GET (ms)', 'Median response time POST (ms)',
          '99th response time GET (ms)', '99th response time POST (ms)',
          'Min response time GET (ms)', 'Min response time POST (ms)',
          'Max response time GET (ms)', 'Max response time POST (ms)']

num_thread_groups_list = [10, 20, 30]

# 設置圖表大小和格子
fig, axs = plt.subplots(4, 3, figsize=(12, 16))
fig.suptitle('Performance comparison between Java and Go servers for different numThreadGroups')

# 繪製資料
for i, label in enumerate(labels):
    row = i // 3
    col = i % 3
    ax = axs[row, col]
    java_values = [java_metrics_new[j]['metrics'][i] for j in range(len(num_thread_groups_list))]
    go_values = [go_metrics_new[j]['metrics'][i] for j in range(len(num_thread_groups_list))]
    
    ax.plot(num_thread_groups_list, java_values, marker='o', label='Java Server')
    ax.plot(num_thread_groups_list, go_values, marker='x', label='Go Server')
    
    ax.set_title(label)
    ax.set_xlabel('Num Thread Groups')
    ax.set_ylabel('Value')
    ax.set_xticks(num_thread_groups_list)
    ax.legend()

plt.tight_layout(rect=[0, 0.03, 1, 0.97])
plt.show()
plt.savefig("/Users/lin99nn/Documents/cs6650/assignment1/report/client-part2/output1.png")
