 git ls-tree --name-only -z -r HEAD|egrep -z -Z -E '\.(java)$' \
       |xargs -0 -n1 git blame --line-porcelain|grep "^author "|sort|uniq -c|sort -nr
