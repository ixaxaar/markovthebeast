//features that don't use any information of the tokens, only edges


//bias of having an edge at all
weight bias : Double-;
factor:
  for Int h, Int m
  if word(h,_) & word(m,_)
  add [link(h,m)] * bias;

set collector.all.bias = true;

//bias of having an edge with a particular label
weight bias_l : Dep -> Double;
factor:
  for Int h, Int m, Dep l
  if word(h,_) & word(m,_)
  add [dep(h,m,l)] * bias_l(l);

set collector.all.bias_l = true;
