#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i cora-all.mln -o cora-all.trained-for-fold0.mln \
#   -t 1of10.fold.db,2of10.fold.db,3of10.fold.db,4of10.fold.db,6of10.fold.db,7of10.fold.db,8of10.fold.db,9of10.fold.db \
#   -d true -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true


for i in 0 1 2 3 4 5 6 7 8 9; do
~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i corpora/cora/cora-all.mln -o weights/pl-$i.mln \
   -t corpora/cora/folds/corarest-$i.db \
   -g true -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true  
done;



~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold0of10.mln \
   -t clean.1of10.doublefold.db,clean.2of10.doublefold.db,clean.3of10.doublefold.db,clean.4of10.doublefold.db,clean.5of10.doublefold.db,clean.6of10.doublefold.db,clean.7of10.doublefold.db,clean.8of10.doublefold.db,clean.9of10.doublefold.db \
   -g true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold0of10.mln \
#   -t 2of20.fold.db,3of20.fold.db,4of20.fold.db,5of20.fold.db,6of20.fold.db,7of20.fold.db,8of20.fold.db,9of20.fold.db,10of20.fold.db,11of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold1of10.mln \
#   -t 0of20.fold.db,1of20.fold.db,4of20.fold.db,5of20.fold.db,6of20.fold.db,7of20.fold.db,8of20.fold.db,9of20.fold.db,10of20.fold.db,11of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold2of10.mln \
#   -t 0of20.fold.db,1of20.fold.db,2of20.fold.db,3of20.fold.db,6of20.fold.db,7of20.fold.db,8of20.fold.db,9of20.fold.db,10of20.fold.db,11of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold3of10.mln \
#   -t 0of20.fold.db,1of20.fold.db,2of20.fold.db,3of20.fold.db,4of20.fold.db,5of20.fold.db,8of20.fold.db,9of20.fold.db,10of20.fold.db,11of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold4of10.mln \
#   -t 0of20.fold.db,1of20.fold.db,2of20.fold.db,3of20.fold.db,4of20.fold.db,5of20.fold.db,6of20.fold.db,7of20.fold.db,10of20.fold.db,11of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true

#~/opt/alchemy/bin/learnwts -noAddUnitClauses true -i ../cora-all.mln -o cora-all.trained-for-fold5of10.mln \
#   -t 0of20.fold.db,1of20.fold.db,2of20.fold.db,3of20.fold.db,4of20.fold.db,5of20.fold.db,6of20.fold.db,7of20.fold.db,8of20.fold.db,9of20.fold.db,12of20.fold.db,13of20.fold.db,14of20.fold.db,15of20.fold.db,16of20.fold.db,17of20.fold.db,18of20.fold.db,19of20.fold.db \
#   -d true -dNumIter 100 -dLearningRate 5e-7 -dZeroInit -ne SameBib,SameAuthor,SameTitle,SameVenue -infer "-m -lazy" -multipleDatabases true
