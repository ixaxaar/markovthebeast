
factor:
for Int r, Int h, Int c
add [dep(r,h,a) & link(h,c) => dep(h, c, b)] * w(a,b,bins(20,h-m));

factor:
for Int r, Int h, Int c1, Int c2, Dep l1, Dep l2;
add [dep(r,h,l1) & dep(h,c1,l2) & link(h,c2) => dep(h, c2, l3)] * w(l1,l2,bins(20,h-c1),bins(20,h-c2));