import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('./nbDirectDependencies.csv')

mean_value = df['nbDependencies'].mean()

plt.figure(figsize=(10, 6))
plt.boxplot(df['nbDependencies'],  showfliers=False)

plt.axhline(y=mean_value, color='r', linestyle='--', label=f'Mean: {mean_value:.2f}')

plt.legend()

plt.ylabel("Direct dependdency number")

plt.savefig('boxplotNbDependencies.png', dpi=300)
plt.show()
