import pandas as pd
import matplotlib.pyplot as plt
import os
import matplotlib.ticker as ticker

def format_y_axis(value, tick_number):
    if value >= 1000:
        value = int(value / 1000)
        return f'{value}k'
    else:
        return int(value)

file1Path_dependents = './com.fasterxml.jackson.core_jackson-databind.csv'
file2Path_dependents = './com.google.code.gson_gson.csv'
file1Path_cve = './com.fasterxml.jackson.core_jackson-databind_CVE.csv'
file2Path_cve = './com.google.code.gson_gson_CVE.csv'

df1_dependents = pd.read_csv(file1Path_dependents)
df2_dependents = pd.read_csv(file2Path_dependents)
df1_cve = pd.read_csv(file1Path_cve)
df2_cve = pd.read_csv(file2Path_cve)

file1Name_dependents = os.path.splitext(os.path.basename(file1Path_dependents))[0]
file2Name_dependents = os.path.splitext(os.path.basename(file2Path_dependents))[0]
file1Name_cve = os.path.splitext(os.path.basename(file1Path_cve))[0]
file2Name_cve = os.path.splitext(os.path.basename(file2Path_cve))[0]

for df in [df1_dependents, df2_dependents, df1_cve, df2_cve]:
    df['line'] = range(1, len(df) + 1)

fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(6, 4))

# Sous-graphique pour les dépendants
ax1.plot(df1_dependents['line'], df1_dependents['nbDependents'], marker='o', label="Jackson-databind")
ax1.plot(df2_dependents['line'], df2_dependents['nbDependents'], marker='x', label="Gson")
ax1.set_xlabel('Version position')
ax1.set_ylabel('#Dependents')
ax1.set_xticks(df1_dependents['line']) 
ax1.get_yaxis().set_major_formatter(plt.FuncFormatter(format_y_axis))
ax1.legend()
ax1.grid(True)

# Sous-graphique pour les CVE
ax2.plot(df1_cve['line'], df1_cve['nbCveAggregated'], marker='o', label="Jackson-databind")
ax2.plot(df2_cve['line'], df2_cve['nbCveAggregated'], marker='x', label="Gson")
ax2.set_xlabel('Version position')
ax2.set_ylabel('#Aggregated CVE')
ax2.set_xticks(df1_cve['line'])
ax2.yaxis.set_major_locator(ticker.MaxNLocator(integer=True))
ax2.legend()
ax2.grid(True)

plt.subplots_adjust(bottom=0.15) 
plt.tight_layout()
plt.savefig('libCompare_figure.png', dpi=300)
plt.show()
