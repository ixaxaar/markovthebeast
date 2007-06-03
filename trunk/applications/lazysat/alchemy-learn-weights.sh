~/opt/alchemy/bin/learnwts -i cora-all.mln -o cora-all.trained.mln -t fold0of10.fold -noAddUnitClauses \
  -d true -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy"

  ~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i cora-all.mln -o cora-all.trained.mln \
   -t fold1of10.fold,fold2of10.fold,fold3of10.fold,fold4of10.fold,fold5of10.fold,fold6of10.fold,fold7of10.fold,fold8of10.fold,fold9of10.fold -d true -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true 

