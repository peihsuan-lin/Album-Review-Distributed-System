import matplotlib.pyplot as plt
import numpy as np

labels = ['Mean Response Time (POST)', 'Median Response Time (POST)', '99th Percentile Response Time (POST)', 'Min Response Time (POST)', 'Max Response Time (POST)',
          'Mean Response Time (GET)', 'Median Response Time (GET)', '99th Percentile Response Time (GET)', 'Min Response Time (GET)', 'Max Response Time (GET)']

java_metrics = [60.570394413363275, 48, 275, 13, 8297, 57.179109634551494, 46, 242, 11, 8297]
go_metrics = [59.431714285714285, 48, 525, 12, 1168, 56.59259468438538, 47, 522, 11, 1180]

x = np.arange(len(labels))  # the label locations
width = 0.35  # the width of the bars

# Plotting
fig, ax = plt.subplots(figsize=(14, 8))
rects1 = ax.barh(x - width/2, java_metrics, width, label='Java Server')
rects2 = ax.barh(x + width/2, go_metrics, width, label='Go Server')

# Add some text for labels, title and custom x-axis tick labels, etc.
ax.set_xlabel('Metrics')
ax.set_title('Performance Comparison Between Java and Go Server')
ax.set_yticks(x)
ax.set_yticklabels(labels)
ax.legend()

# Annotate with exact values
for i, v in enumerate(java_metrics):
    ax.text(v + 10, i - width/2, str(round(v, 2)),verticalalignment='center')

for i, v in enumerate(go_metrics):
    ax.text(v + 10, i + width/2, str(round(v, 2)), verticalalignment='center')

plt.show()
