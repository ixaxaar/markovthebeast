~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i cora-all.mln -o cora-all.trained-for-fold0.mln \
   -t 1of10.fold.db,2of10.fold.db,3of10.fold.db,4of10.fold.db,5of10.fold.db,6of10.fold.db,7of10.fold.db,8of10.fold.db,9of10.fold.db \
   -d true -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

