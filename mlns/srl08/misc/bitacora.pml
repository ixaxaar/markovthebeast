
ID: 060608_234611
First model, replicating CoNLL-08 with MST instead of MALT
F1 scores
          isArgument    : 0.746,0.752,0.754,0.756,0.755,0.753,0.753,0.753
              Global    : 0.627,0.634,0.637,0.639,0.638,0.636,0.635,0.635
                role    : 0.446,0.461,0.466,0.469,0.469,0.467,0.465,0.465
         isPredicate    : 0.771,0.773,0.774,0.778,0.777,0.775,0.774,0.777
            hasLabel    : 0.610,0.617,0.619,0.620,0.619,0.616,0.615,0.615
          frameLabel    : 0.676,0.681,0.684,0.684,0.685,0.681,0.681,0.683

ID:060708_035219
Fixes global formula missing from role hard.
          isArgument    : 0.753,0.760,0.760,0.761,0.760,0.758,0.757,0.757
              Global    : 0.638,0.644,0.646,0.646,0.645,0.643,0.642,0.642
                role    : 0.464,0.475,0.480,0.481,0.481,0.479,0.479,0.479
         isPredicate    : 0.777,0.781,0.783,0.782,0.782,0.780,0.779,0.780
            hasLabel    : 0.623,0.628,0.630,0.627,0.626,0.624,0.622,0.622
          frameLabel    : 0.685,0.690,0.691,0.688,0.687,0.685,0.683,0.683


ID: 060808_215243 
Fixes the observable predicates. This means previous results don't include the dep parsing features. 
F1 scores
          isArgument    : 0.803,0.805,0.803,0.802,0.799,0.797,0.796,0.795
              Global    : 0.687,0.692,0.693,0.692,0.689,0.686,0.686,0.685
                role    : 0.516,0.529,0.532,0.532,0.531,0.528,0.527,0.527
         isPredicate    : 0.811,0.811,0.813,0.812,0.808,0.807,0.808,0.807
            hasLabel    : 0.687,0.692,0.691,0.690,0.686,0.684,0.682,0.682
          frameLabel    : 0.712,0.714,0.717,0.715,0.710,0.709,0.710,0.708

ID:060908_144259
MALT + MST 
F1 scores
          isArgument    : 0.807,0.807,0.805,0.803,0.802,0.800,0.800,0.799
              Global    : 0.691,0.695,0.694,0.694,0.693,0.691,0.690,0.690
                role    : 0.517,0.529,0.531,0.534,0.534,0.532,0.532,0.532
         isPredicate    : 0.814,0.814,0.810,0.810,0.810,0.807,0.806,0.805
            hasLabel    : 0.697,0.701,0.699,0.698,0.696,0.694,0.693,0.693
          frameLabel    : 0.711,0.716,0.713,0.713,0.711,0.708,0.707,0.707


ID:
  Adds siblingns, takes out WNet
         isArgument    : 0.804,0.806,0.804,0.804,0.803,0.800,0.800,0.798
             Global    : 0.688,0.695,0.693,0.693,0.691,0.689,0.687,0.685
               role    : 0.511,0.526,0.525,0.525,0.523,0.523,0.521,0.519
        isPredicate    : 0.814,0.815,0.812,0.813,0.812,0.810,0.808,0.806
           hasLabel    : 0.695,0.701,0.698,0.698,0.697,0.694,0.691,0.689
         frameLabel    : 0.712,0.716,0.716,0.715,0.713,0.712,0.710,0.707 



ID:061208_232812
 possiblePredicate & possibleArgument

F1 scores
          isArgument    : 0.805,0.808,0.807,0.805,0.803,0.801,0.799,0.797
              Global    : 0.686,0.694,0.693,0.692,0.690,0.688,0.686,0.684
                role    : 0.504,0.523,0.521,0.521,0.521,0.520,0.520,0.517
         isPredicate    : 0.813,0.815,0.814,0.812,0.811,0.809,0.808,0.807
            hasLabel    : 0.696,0.701,0.699,0.698,0.695,0.693,0.690,0.687
          frameLabel    : 0.710,0.714,0.717,0.713,0.711,0.710,0.709,0.708

---------------------------
EACL experiments (06/09/08)
---------------------------

Links
-----

2005 Data : http://www.lsi.upc.edu/~srlconll/soft.html
Mihai data: Provided by direct contact (mail, hopepage: http://www.surdeanu.name/mihai/publications.php)
2008 Data : http://www.yr-bcn.es/conll2008/
Script constituents to deps: http://nlp.cs.lth.se/pennconverter/


Creating the corpora
--------------------

We need to translate 2005 corpora into 2008 format. For this we use Mihai files which are stored in:
    data/conll05/*.output

There is an error in the amount of utts in 2005 and 2008, since we have a mst 2008 corpus trained, we need to generate the file again with:

./scripts/translatecorpora2beast_eacl

This synchronises the corpora:

    [dendrite]s0343487: grep '>>' data/conll05/train_propbank.atoms |  wc
     39279   39279  117837
    [dendrite]s0343487: grep '>>' data/train/train.open.atoms | wc
     39279   39279  117837
    [dendrite]s0343487: grep '>>' data/conll05/devel_propbank.atoms |  wc
     1334    1334    4002
    [dendrite]s0343487: grep '>>' data/devel/devel.open.atoms | wc
     1334    1334    4002


Generating a similar setup than CONLL2005
-----------------------------------------

Mihai files contain gold standard dependencies parses (produced from the Penn TreeBank). In order to have a similar setup than Conll05 we need Charniak parses, to be translated to dependencies.

Translating 2005 charniak parses into Penn Treebank Format

1. Extract charniak parses from conll05
::
    ./scripts/generate_charniakparses
:: 
    ./scripts/translatecorpora2beast_eacl_possiblePred_chk

Experiments with 1000 training utterances:
bin/results.py results/devel_propbank.open.091908_165545.log 
F1 scores
            hasLabel    : 0.756,0.754,0.752,0.749,0.745
              Global    : 0.675,0.679,0.680,0.678,0.676
                role    : 0.498,0.508,0.512,0.511,0.510
          isArgument    : 0.784,0.788,0.788,0.788,0.786

MODELLING
---------

FULL_MODEL+MST-WNET

This is the 2008 model results:
 bin/results.py results/devel.open.090608_183203.log 
 F1 scores
          isArgument    : 0.850,0.855,0.856,0.854,0.854
              Global    : 0.767,0.776,0.778,0.777,0.777
                role    : 0.652,0.668,0.672,0.672,0.673
         isPredicate    : 0.866,0.871,0.873,0.873,0.874
            hasLabel    : 0.758,0.766,0.767,0.766,0.766
          frameLabel    : 0.784,0.792,0.793,0.793,0.794


BASELINE

This is the 2005 model results:

+ conditionated on possiblePredicate and possibleArgument
bin/results.py results/devel_propbank.open.091408_223804.log
F1 scores
          isArgument    : 0.874,0.874,0.873,0.873,0.872
              Global    : 0.825,0.829,0.829,0.829,0.828
                role    : 0.712,0.724,0.726,0.725,0.725
         isPredicate    : 0.949,0.948,0.948,0.946,0.945
            hasLabel    : 0.833,0.836,0.836,0.835,0.833
          frameLabel    : 0.859,0.859,0.858,0.861,0.859


+ We generate the corpus with possiblePredicate based on actual predicates, and only perform argument classification and role identification
bin/results.py results/devel_propbank.open.091608_000428.log
F1 scores
            hasLabel    : 0.844,0.847,0.847,0.846,0.845
              Global    : 0.814,0.821,0.822,0.822,0.821
                role    : 0.716,0.734,0.737,0.737,0.736
          isArgument    : 0.889,0.891,0.890,0.890,0.890

+ We switch to files using the dependencies from the conll05 (constituency to deps, charniak parses)
bin/results.py results/devel_propbank.open.092408_205233.log
F1 scores
            hasLabel    : 0.856,0.858,0.859,0.859,0.858
              Global    : 0.821,0.826,0.828,0.829,0.829
                role    : 0.721,0.734,0.738,0.740,0.742
          isArgument    : 0.893,0.893,0.895,0.895,0.895

+ We change the training (4 epochs per iter, one iter). The result:
bin/results.py results/devel_propbank.open.092508_013243.log
F1 scores
            hasLabel    : 0.858
              Global    : 0.829
                role    : 0.742
          isArgument    : 0.895


+ Bottom-up model
bin/results.py results/devel_propbank.open.092508_141640.log
F1 scores
            hasLabel    : 0.876,0.879,0.880,0.879,0.879
              Global    : 0.841,0.846,0.849,0.849,0.849
                role    : 0.741,0.752,0.758,0.760,0.760
          isArgument    : 0.910,0.913,0.913,0.913,0.913

+ EVALUATION
bin/results.py  results/test_propbank.092508_141640.log
F1 scores
            hasLabel    : 0.891
              Global    : 0.865
                role    : 0.786
          isArgument    : 0.920




 



