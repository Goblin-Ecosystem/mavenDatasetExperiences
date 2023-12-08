import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('./cveOccurrenceByTimestamp.csv')
df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')

df_grouped = df.resample('1Y', on='timestamp').sum()

bar_positions = [i for i in range(len(df_grouped))]

fig, ax = plt.subplots()
ax.bar(bar_positions, df_grouped['nbCVE'], width=0.8, align='edge')

labels = [date.strftime('%Y') for date in df_grouped.index]
ax.set_xticks([i for i in range(len(df_grouped))])
ax.set_xticklabels(labels, rotation=45)

plt.xlabel('1-year interval')
plt.ylabel('Number of CVEs')

plt.tight_layout()
plt.savefig('CVE_evolution.png', dpi=300)
plt.show()
