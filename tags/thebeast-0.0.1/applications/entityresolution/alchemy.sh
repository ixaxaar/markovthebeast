 #!/bin/bash

mln=$1
db=$2
out=$3

 ~/opt/alchemy/bin/infer -i $mln -e $db -r $out \
 -q SameBib,SameTitle,SameAuthor,SameVenue -lazy -m 