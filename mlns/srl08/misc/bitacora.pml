
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
-------

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
This gives a better result than the rerun because, in this model the following formulae is missing:
//factor [0]: for Int a, Int p if word(a,_) & word(p,_) & possiblePredicate(p) & possibleArgument(a): hasLabel(p,a) => |Role r: role(p,a,r)| >=1;

+ Rerun previous experiment using the latest version of the formulae.
bin/results.py results/devel_propbank.open.100608_223017.log
F1 scores
            hasLabel    : 0.855,0.857,0.858,0.858,0.857
              Global    : 0.811,0.815,0.818,0.819,0.818
                role    : 0.709,0.721,0.726,0.728,0.727
          isArgument    : 0.876,0.877,0.878,0.878,0.878

+ EVAL(5)
bin/results.py results/devel_propbank.open.100608_22301.test.log 
F1 scores
            hasLabel    : 0.867,0.822
              Global    : 0.836,0.767
                role    : 0.757,0.640
          isArgument    : 0.891,0.850



+ Fixing error on script (it was training with s2 files)
bin/results.py results/devel_propbank.open.100708_175118.log
F1 scores
            hasLabel    : 0.858,0.859,0.858,0.859,0.859
              Global    : 0.822,0.827,0.829,0.830,0.831
                role    : 0.723,0.735,0.741,0.744,0.745
          isArgument    : 0.894,0.895,0.894,0.896,0.896

EVAL(5)
bin/results.py results/devel_propbank.open.100708_175118.test.log
F1 scores
            hasLabel    : 0.875,0.847
              Global    : 0.849,0.799
                role    : 0.771,0.674
          isArgument    : 0.907,0.886

FINAL EVAL
WSJ
 SEMANTIC SCORES: 
  Labeled precision:          (10593 + 5260) / (13227 + 5260) * 100 = 85.75 %
  Labeled recall:             (10593 + 5260) / (14269 + 5260) * 100 = 81.18 %
  Labeled F1:                 83.40 
  Unlabeled precision:        (11945 + 5260) / (13227 + 5260) * 100 = 93.07 %
  Unlabeled recall:           (11945 + 5260) / (14269 + 5260) * 100 = 88.10 %
  Unlabeled F1:               90.51 

BROWN
  SEMANTIC SCORES: 
  Labeled precision:          (1409 + 804) / (2004 + 805) * 100 = 78.78 %
  Labeled recall:             (1409 + 804) / (2210 + 804) * 100 = 73.42 %
  Labeled F1:                 76.01 
  Unlabeled precision:        (1763 + 804) / (2004 + 805) * 100 = 91.38 %
  Unlabeled recall:           (1763 + 804) / (2210 + 804) * 100 = 85.17 %
  Unlabeled F1:               88.17 



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

+ Using charniak parses and bottom-up model
bin/results.py results/devel_propbank.open.093008_222007.log
F1 scores
          isArgument    : 0.885,0.883,0.881,0.880,0.879
              Global    : 0.826,0.828,0.829,0.827,0.826
                role    : 0.721,0.733,0.737,0.737,0.736
         isPredicate    : 0.937,0.934,0.932,0.929,0.927
            hasLabel    : 0.854,0.855,0.853,0.851,0.850
          frameLabel    : 0.770,0.770,0.774,0.767,0.768

+ Using charniak parses and full model
bin/results.py results/devel_propbank.open.100108_125036.log
F1 scores
          isArgument    : 0.882,0.884,0.882,0.881,0.878
              Global    : 0.825,0.828,0.827,0.825,0.824
                role    : 0.725,0.736,0.737,0.738,0.737
         isPredicate    : 0.936,0.934,0.931,0.929,0.927
            hasLabel    : 0.854,0.855,0.853,0.852,0.850
          frameLabel    : 0.760,0.754,0.754,0.746,0.747

EVAL(5)
bin/results.py results/devel_propbank.open.100108_125036.test.log
F1 scores
          isArgument    : 0.892
              Global    : 0.851
                role    : 0.760
         isPredicate    : 0.952
            hasLabel    : 0.862
          frameLabel    : 0.867


+ Fixing the global rule
bin/results.py results/devel_propbank.open.100108_185801.log
F1 scores
          isArgument    : 0.878,0.879,0.880,0.880,0.880
              Global    : 0.829,0.833,0.836,0.837,0.837
                role    : 0.710,0.722,0.730,0.733,0.734
         isPredicate    : 0.944,0.945,0.945,0.945,0.944
            hasLabel    : 0.850,0.851,0.853,0.854,0.852
          frameLabel    : 0.854,0.859,0.858,0.857,0.858

+ BASELINE(EVAL(5))
bin/results.py results/devel_propbank.open.100108_185801.test.log
F1 scores
          isArgument    : 0.891,0.858
              Global    : 0.851,0.784
                role    : 0.760,0.645
         isPredicate    : 0.953,0.915
            hasLabel    : 0.862,0.820
          frameLabel    : 0.866,0.752


FINAL_EVAL
  WSJ
    SEMANTIC SCORES: 
    Labeled precision:          (10381 + 4496) / (13061 + 5121) * 100 = 81.82 %
    Labeled recall:             (10381 + 4496) / (14269 + 5260) * 100 = 76.18 %
    Labeled F1:                 78.90 
    Unlabeled precision:        (11598 + 4945) / (13061 + 5121) * 100 = 90.99 %
    Unlabeled recall:           (11598 + 4945) / (14269 + 5260) * 100 = 84.71 %
    Unlabeled F1:               87.74 

Brown
    SEMANTIC SCORES: 
    Labeled precision:          (1357 + 603) / (2033 + 800) * 100 = 69.18 %
    Labeled recall:             (1357 + 603) / (2210 + 804) * 100 = 65.03 %
    Labeled F1:                 67.04 
    Unlabeled precision:        (1694 + 733) / (2033 + 800) * 100 = 85.67 %
    Unlabeled recall:           (1694 + 733) / (2210 + 804) * 100 = 80.52 %
    Unlabeled F1:               83.02 

+ Taking out isPredicate(p) => exists a. hasLabel(p,a)
  Taking out isArgument(a)  => exists p. hasLabel(p,a)

bin/results.py results/devel_propbank.open.100908_141042.log
F1 scores
          isArgument    : 0.895,0.896,0.895,0.894,0.894
              Global    : 0.840,0.845,0.845,0.847,0.847
                role    : 0.706,0.720,0.722,0.726,0.728
         isPredicate    : 0.960,0.959,0.959,0.959,0.958
            hasLabel    : 0.869,0.872,0.872,0.871,0.871
          frameLabel    : 0.867,0.864,0.865,0.871,0.872

+ EVAL(5)
bin/results.py results/devel_propbank.open.100908_141042.test.log
F1 scores
          isArgument    : 0.891,0.855
              Global    : 0.852,0.789
                role    : 0.763,0.654
         isPredicate    : 0.950,0.913
            hasLabel    : 0.865,0.829
          frameLabel    : 0.866,0.760

Final eval
WSJ
  SEMANTIC SCORES: 
  Labeled precision:          (10353 + 4484) / (12864 + 5099) * 100 = 82.60 %
  Labeled recall:             (10353 + 4484) / (14269 + 5260) * 100 = 75.97 %
  Labeled F1:                 79.15 
  Unlabeled precision:        (11555 + 4921) / (12864 + 5099) * 100 = 91.72 %
  Unlabeled recall:           (11555 + 4921) / (14269 + 5260) * 100 = 84.37 %
  Unlabeled F1:               87.89 
Brown
  SEMANTIC SCORES: 
  Labeled precision:          (1365 + 603) / (1998 + 783) * 100 = 70.77 %
  Labeled recall:             (1365 + 603) / (2210 + 804) * 100 = 65.30 %
  Labeled F1:                 67.92 
  Unlabeled precision:        (1698 + 724) / (1998 + 783) * 100 = 87.09 %
  Unlabeled recall:           (1698 + 724) / (2210 + 804) * 100 = 80.36 %
  Unlabeled F1:               83.59 


+ Taking out isPredicate(p) => exists a. hasLabel(p,a)
bin/results.py results/devel_propbank.open.101508_142150.log
F1 scores
          isArgument    : 0.879,0.880,0.880,0.880,0.879
              Global    : 0.830,0.835,0.836,0.836,0.836
                role    : 0.709,0.722,0.727,0.729,0.732
         isPredicate    : 0.948,0.950,0.949,0.949,0.948
            hasLabel    : 0.851,0.853,0.852,0.851,0.851
          frameLabel    : 0.858,0.861,0.860,0.861,0.860

+ EVAL(5)
bin/results.py results/devel_propbank.open.101508_142150.test.log
F1 scores
          isArgument    : 0.892,0.861
              Global    : 0.852,0.787
                role    : 0.759,0.650
         isPredicate    : 0.958,0.915
            hasLabel    : 0.863,0.825
          frameLabel    : 0.871,0.749





GOLD dependecies
================
+ Using gold dependencies, no predicate identification
bin/results.py results/devel_propbank.open.092808_230928.log
F1 scores
            hasLabel    : 0.944,0.945,0.945,0.945,0.945
              Global    : 0.909,0.913,0.915,0.916,0.916
                role    : 0.824,0.835,0.838,0.841,0.840
          isArgument    : 0.966,0.965,0.966,0.966,0.966

+ EVALUATION
bin/results.py results/devel_propbank.open.092808_230928.test.log
F1 scores
            hasLabel    : 0.957
              Global    : 0.931
                role    : 0.866
          isArgument    : 0.972


+ Using gold dependencies, with predicate identification (Upper boundery)
bin/results.py results/devel_propbank.open.092908_193840.log
F1 scores
          isArgument    : 0.955,0.956,0.957,0.957,0.958
              Global    : 0.900,0.906,0.907,0.907,0.908
                role    : 0.820,0.831,0.834,0.836,0.838
         isPredicate    : 0.987,0.987,0.987,0.987,0.987
            hasLabel    : 0.934,0.936,0.937,0.937,0.938
          frameLabel    : 0.803,0.824,0.821,0.816,0.818
ERROR: it's necessary to recreate the corpus without the possible_ON option :(

bin/results.py results/devel_propbank.open.101008_175346.log
F1 scores
          isArgument    : 0.944,0.946,0.945,0.945,0.945
              Global    : 0.895,0.900,0.901,0.902,0.902
                role    : 0.796,0.805,0.811,0.812,0.813
         isPredicate    : 0.961,0.962,0.962,0.962,0.962
            hasLabel    : 0.935,0.937,0.937,0.937,0.937
          frameLabel    : 0.873,0.880,0.879,0.883,0.882

EVAL (5)
bin/results.py results/devel_propbank.open.101008_175346.test.log
F1 scores
          isArgument    : 0.956,0.919
              Global    : 0.916,0.851
                role    : 0.842,0.739
         isPredicate    : 0.971,0.931
            hasLabel    : 0.944,0.908
          frameLabel    : 0.888,0.761

Final eval
 WSJ
 SEMANTIC SCORES: 
  Labeled precision:          (11730 + 4674) / (13603 + 5270) * 100 = 86.92 %
  Labeled recall:             (11730 + 4674) / (14269 + 5260) * 100 = 84.00 %
  Labeled F1:                 85.43 
  Unlabeled precision:        (12958 + 5110) / (13603 + 5270) * 100 = 95.73 %
  Unlabeled recall:           (12958 + 5110) / (14269 + 5260) * 100 = 92.52 %
  Unlabeled F1:               94.10 

 Brown
 SEMANTIC SCORES: 
  Labeled precision:          (1601 + 621) / (2156 + 828) * 100 = 74.46 %
  Labeled recall:             (1601 + 621) / (2210 + 804) * 100 = 73.72 %
  Labeled F1:                 74.09 
  Unlabeled precision:        (1931 + 759) / (2156 + 828) * 100 = 90.15 %
  Unlabeled recall:           (1931 + 759) / (2210 + 804) * 100 = 89.25 %
  Unlabeled F1:               89.70 

+ Fixing error (It wasn't the full model)
bin/results.py results/devel_propbank.open.101308_133546.log
F1 scores
          isArgument    : 0.937,0.939,0.939,0.939,0.939
              Global    : 0.890,0.895,0.896,0.896,0.896
                role    : 0.800,0.811,0.814,0.816,0.816
         isPredicate    : 0.952,0.953,0.953,0.953,0.953
            hasLabel    : 0.926,0.928,0.929,0.929,0.929
          frameLabel    : 0.858,0.868,0.868,0.868,0.868

+ EVAL(5)
bin/results.py results/devel_propbank.open.101308_133546.test.log
F1 scores
          isArgument    : 0.946,0.919
              Global    : 0.909,0.848
                role    : 0.847,0.740
         isPredicate    : 0.959,0.921
            hasLabel    : 0.934,0.902
          frameLabel    : 0.873,0.755

FINAL eval
 WSJ
 SEMANTIC SCORES: 
  Labeled precision:          (11718 + 4545) / (13391 + 5153) * 100 = 87.70 %
  Labeled recall:             (11718 + 4545) / (14269 + 5260) * 100 = 83.28 %
  Labeled F1:                 85.43 
  Unlabeled precision:        (12722 + 4993) / (13391 + 5153) * 100 = 95.53 %
  Unlabeled recall:           (12722 + 4993) / (14269 + 5260) * 100 = 90.71 %
  Unlabeled F1:               93.06 


 Brown
  SEMANTIC SCORES: 
  Labeled precision:          (1591 + 609) / (2123 + 811) * 100 = 74.98 %
  Labeled recall:             (1591 + 609) / (2210 + 804) * 100 = 72.99 %
  Labeled F1:                 73.97 
  Unlabeled precision:        (1903 + 743) / (2123 + 811) * 100 = 90.18 %
  Unlabeled recall:           (1903 + 743) / (2210 + 804) * 100 = 87.79 %
  Unlabeled F1:               88.97 


+ CONLL05 version (predicate known)
::
  bin/results.py results/devel_propbank.open.111708_103832.log
  F1 scores
          isArgument    : 0.967,0.967,0.968,0.968,0.968
              Global    : 0.916,0.919,0.920,0.921,0.921
                role    : 0.811,0.822,0.824,0.827,0.828
         isPredicate    : 1.000,1.000,1.000,1.000,1.000
            hasLabel    : 0.947,0.947,0.948,0.948,0.947
          frameLabel    : 0.910,0.908,0.910,0.909,0.909

+Eval(5)
::
  bin/results.py results/devel_propbank.open.111708_103832.test.log
F1 scores
          isArgument    : 0.973,0.956
              Global    : 0.933,0.891
                role    : 0.855,0.770
         isPredicate    : 1.000,1.000
            hasLabel    : 0.957,0.936
          frameLabel    : 0.911,0.832


Final eval
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (11959 + 4792) / (13699 + 5260) * 100 = 88.35 %
  Labeled recall:             (11959 + 4792) / (14269 + 5260) * 100 = 85.78 %
  Labeled F1:                 87.05 
  Unlabeled precision:        (13287 + 5260) / (13699 + 5260) * 100 = 97.83 %
  Unlabeled recall:           (13287 + 5260) / (14269 + 5260) * 100 = 94.97 %
  Unlabeled F1:               96.38 

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1658 + 669) / (2128 + 805) * 100 = 79.34 %
  Labeled recall:             (1658 + 669) / (2210 + 804) * 100 = 77.21 %
  Labeled F1:                 78.26 
  Unlabeled precision:        (2008 + 804) / (2128 + 805) * 100 = 95.87 %
  Unlabeled recall:           (2008 + 804) / (2210 + 804) * 100 = 93.30 %
  Unlabeled F1:               94.57 


+ Fixing that isPredicate was hidden but observable (include hard)
::
  bin/results.py results/devel_propbank.open.112008_190753.log
  F1 scores
            hasLabel    : 0.946,0.947,0.947,0.947,0.947
              Global    : 0.907,0.910,0.912,0.912,0.912
                role    : 0.813,0.822,0.827,0.827,0.828
          frameLabel    : 0.907,0.912,0.911,0.911,0.913
          isArgument    : 0.968,0.967,0.968,0.968,0.968

Eval(5)
::
  bin/results.py results/devel_propbank.open.112008_190753.test.log
  F1 scores
            hasLabel    : 0.958,0.939
              Global    : 0.926,0.880
                role    : 0.857,0.773
          frameLabel    : 0.911,0.826
          isArgument    : 0.973,0.958

Final eval
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (11990 + 4790) / (13726 + 5260) * 100 = 88.38 %
  Labeled recall:             (11990 + 4790) / (14269 + 5260) * 100 = 85.92 %
  Labeled F1:                 87.13 
  Unlabeled precision:        (13314 + 5260) / (13726 + 5260) * 100 = 97.83 %
  Unlabeled recall:           (13314 + 5260) / (14269 + 5260) * 100 = 95.11 %
  Unlabeled F1:               96.45 

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1663 + 664) / (2128 + 805) * 100 = 79.34 %
  Labeled recall:             (1663 + 664) / (2210 + 804) * 100 = 77.21 %
  Labeled F1:                 78.26 
  Unlabeled precision:        (2013 + 804) / (2128 + 805) * 100 = 96.05 %
  Unlabeled recall:           (2013 + 804) / (2210 + 804) * 100 = 93.46 %
  Unlabeled F1:               94.74 


+ Using j08 output (not direct comparison since some sententences are missing)
::
  bin/results.py results/devel_propbank.open.112008_190753.test.j08deps.log
  F1 scores
            hasLabel    : 0.959,0.940
              Global    : 0.927,0.882
                role    : 0.858,0.777
          frameLabel    : 0.910,0.824
          isArgument    : 0.975,0.958


WSJ (2396 of 2414)
::
  head -13 results/test_wsj_propbank.112008_190753.5.j08deps.eval
  SEMANTIC SCORES:
  Labeled precision:          (11884 + 4738) / (13590 + 5206) * 100 = 88.43 %
  Labeled recall:             (11884 + 4738) / (14121 + 5206) * 100 = 86.00 %
  Labeled F1:                 87.20
  Unlabeled precision:        (13198 + 5206) / (13590 + 5206) * 100 = 97.91 %
  Unlabeled recall:           (13198 + 5206) / (14121 + 5206) * 100 = 95.22 %
  Unlabeled F1:               96.55


Brown (425 of 426)
::
  head -13 results/test_brown_propbank.112008_190753.5.j08deps.eval
  SEMANTIC SCORES:
  Labeled precision:          (1668 + 661) / (2123 + 803) * 100 = 79.60 %
  Labeled recall:             (1668 + 661) / (2203 + 802) * 100 = 77.50 %
  Labeled F1:                 78.54
  Unlabeled precision:        (2010 + 802) / (2123 + 803) * 100 = 96.10 %
  Unlabeled recall:           (2010 + 802) / (2203 + 802) * 100 = 93.58 %
  Unlabeled F1:               94.82



PIPELINE experiments
--------------------

+ Modeling  isPredicate and frameLabel stage (predicate classification)
Precision scores
         isPredicate    : 0.988,0.992,0.993,0.992,0.993
              Global    : 0.904,0.906,0.897,0.891,0.883
          frameLabel    : 0.820,0.821,0.801,0.789,0.772
Recall scores
         isPredicate    : 0.826,0.774,0.742,0.716,0.692
              Global    : 0.756,0.708,0.670,0.643,0.615
          frameLabel    : 0.685,0.641,0.599,0.569,0.538
F1 scores
         isPredicate    : 0.900,0.870,0.849,0.832,0.816
              Global    : 0.823,0.795,0.767,0.747,0.725
          frameLabel    : 0.746,0.720,0.685,0.661,0.634

+ Fixing the integer option
bin/results.py results/devel_propbank.open.093008_193110.log
F1 scores
         isPredicate    : 0.903,0.872,0.849,0.827,0.809
              Global    : 0.798,0.766,0.727,0.703,0.682
          frameLabel    : 0.692,0.659,0.605,0.579,0.555

Given the previous results we only do the isPredicate in this stage

+ Only isPredicate (stage 1)
bin/resulfts.py results/devel_propbank.open.093008_205921.log
F1 scores
         isPredicate    : 0.959,0.959,0.958,0.959,0.960
              Global    : 0.959,0.959,0.958,0.959,0.960

EVAL(5)
bin/results.py results/devel_propbank.open.093008_205921.log
F1 scores
         isPredicate    : 0.966,0.920
              Global    : 0.966,0.920

+ isArgument, hasLabel and role (stage 2)
bin/results.py results/devel_propbank.open.100208_173201.log
F1 scores
            hasLabel    : 0.851,0.854,0.854,0.853,0.853
              Global    : 0.808,0.814,0.815,0.816,0.817
                role    : 0.707,0.721,0.724,0.725,0.728
          isArgument    : 0.875,0.876,0.877,0.877,0.876

Eval (5)
::


::
  bin/results.py results/devel_propbank.open.112208_205715.log
  F1 scores
            hasLabel    : 0.869,0.870,0.871,0.871,0.870
              Global    : 0.820,0.824,0.825,0.825,0.825
                role    : 0.710,0.719,0.722,0.721,0.722
          isArgument    : 0.890,0.891,0.891,0.891,0.891

EVAL(5)
::
  bin/results.py results/devel_propbank.open.100208_173201.test.log
  F1 scores
            hasLabel    : 0.863,0.817
              Global    : 0.834,0.766
                role    : 0.756,0.645
          isArgument    : 0.887,0.846

+ frameLabel (stage 3)
::
  bin/results.py results/devel_propbank.open.100308_155627.log
  F1 scores
            Global    : 0.836,0.845,0.844,0.842,0.840
        frameLabel    : 0.836,0.845,0.844,0.842,0.840
EVAL(2)
::
  bin/results.py results/devel_propbank.open.100308_155627.test.log
  F1 scores
              Global    : 0.857,0.687
          frameLabel    : 0.857,0.687


FINAL_EVAL
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (10248 + 4532) / (12826 + 5320) * 100 = 81.45 %
  Labeled recall:             (10248 + 4532) / (14269 + 5260) * 100 = 75.68 %
  Labeled F1:                 78.46 
  Unlabeled precision:        (11455 + 5109) / (12826 + 5320) * 100 = 91.28 %
  Unlabeled recall:           (11455 + 5109) / (14269 + 5260) * 100 = 84.82 %
  Unlabeled F1:               87.93 

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1330 + 565) / (1947 + 842) * 100 = 67.95 %
  Labeled recall:             (1330 + 565) / (2210 + 804) * 100 = 62.87 %
  Labeled F1:                 65.31 
  Unlabeled precision:        (1639 + 757) / (1947 + 842) * 100 = 85.91 %
  Unlabeled recall:           (1639 + 757) / (2210 + 804) * 100 = 79.50 %
  Unlabeled F1:               82.58 


NAACL/HLT08

RERUN pipeline:

+ Stage 1
::
  bin/results.py results/devel_propbank.open.112308_012232.log
  F1 scores
         isPredicate    : 0.958,0.959,0.958,0.959,0.959
              Global    : 0.958,0.959,0.958,0.959,0.959

+ Eval (5)
::
  bin/results.py results/devel_propbank.open.112308_012232.test.log
  F1 scores
         isPredicate    : 0.966,0.922
              Global    : 0.966,0.922

+ Stage 2

Note that I added constraint if isPredicate there should be a hasLabel (this seem to do a good job in j08).
::
  bin/results.py results/devel_propbank.open.112308_151718.log
  F1 scores
            hasLabel    : 0.853,0.855,0.856,0.854,0.852
              Global    : 0.802,0.807,0.808,0.808,0.807
                role    : 0.685,0.694,0.699,0.699,0.700
          isArgument    : 0.877,0.880,0.880,0.879,0.878

Eval 5
::
  bin/results.py results/devel_propbank.open.112308_151718.test.log
  F1 scores
            hasLabel    : 0.863,0.809
              Global    : 0.826,0.752
                role    : 0.731,0.613
          isArgument    : 0.891,0.846

Without previous constraint, there should be a hasLabel.
::
  bin/results.py results/devel_propbank.open.112308_201121.log
  F1 scores
            hasLabel    : 0.870,0.873,0.872,0.872,0.872
              Global    : 0.819,0.825,0.825,0.826,0.826
                role    : 0.706,0.718,0.720,0.722,0.723
          isArgument    : 0.890,0.892,0.891,0.891,0.891

Eval 5
::
  bin/results.py results/devel_propbank.open.112308_201121.test.log
  F1 scores
            hasLabel    : 0.880,0.836
              Global    : 0.843,0.775
                role    : 0.754,0.642
          isArgument    : 0.903,0.859

Stage 3
::
  bin/results.py results/devel_propbank.open.112408_001852.log
  F1 scores
              Global    : 0.838,0.848,0.850,0.848,0.848
          frameLabel    : 0.838,0.848,0.850,0.848,0.848

Eval 3
::
  bin/results.py results/devel_propbank.open.112408_001852.test.log
  F1 scores
              Global    : 0.859,0.691
          frameLabel    : 0.859,0.691

FINAL eval
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (10284 + 4545) / (13021 + 5321) * 100 = 80.85 %
  Labeled recall:             (10284 + 4545) / (14269 + 5260) * 100 = 75.93 %
  Labeled F1:                 78.31 
  Unlabeled precision:        (11757 + 5109) / (13021 + 5321) * 100 = 91.95 %
  Unlabeled recall:           (11757 + 5109) / (14269 + 5260) * 100 = 86.36 %
  Unlabeled F1:               89.07 

Brown
::
  head -13  data/conll05/test_brown_propbank.s3.112408_001852.eval 
  SEMANTIC SCORES: 
  Labeled precision:          (1335 + 570) / (1986 + 846) * 100 = 67.27 %
  Labeled recall:             (1335 + 570) / (2210 + 804) * 100 = 63.21 %
  Labeled F1:                 65.17 
  Unlabeled precision:        (1696 + 760) / (1986 + 846) * 100 = 86.72 %
  Unlabeled recall:           (1696 + 760) / (2210 + 804) * 100 = 81.49 %
  Unlabeled F1:               84.02 

+ Using the output of stage 2 where the isPredicate -> |hasLabel| > 1
::
  bin/results.py results/devel_propbank.open.112508_140406.log
  F1 scores
              Global    : 0.838,0.848,0.850,0.848,0.848
          frameLabel    : 0.838,0.848,0.850,0.848,0.848

Eval (3)
::
  bin/results.py results/devel_propbank.open.112508_140406.test.log
  F1 scores
              Global    : 0.858,0.699
          frameLabel    : 0.858,0.699

Final eval
WSJ
::
  head -13 results/test_wsj_propbank.s3.112508_140406.3.eval
  SEMANTIC SCORES: 
  Labeled precision:          (9880 + 4540) / (12770 + 5321) * 100 = 79.71 %
  Labeled recall:             (9880 + 4540) / (14269 + 5260) * 100 = 73.84 %
  Labeled F1:                 76.66 
  Unlabeled precision:        (11432 + 5109) / (12770 + 5321) * 100 = 91.43 %
  Unlabeled recall:           (11432 + 5109) / (14269 + 5260) * 100 = 84.70 %
  Unlabeled F1:               87.94 

Brown
::
  head -13 results/test_brown_propbank.s3.112508_140406.3.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1272 + 576) / (1972 + 846) * 100 = 65.58 %
  Labeled recall:             (1272 + 576) / (2210 + 804) * 100 = 61.31 %
  Labeled F1:                 63.37 
  Unlabeled precision:        (1635 + 760) / (1972 + 846) * 100 = 84.99 %
  Unlabeled recall:           (1635 + 760) / (2210 + 804) * 100 = 79.46 %
  Unlabeled F1:               82.13 

+ Using new global formula for frameLabel
bin/results.py results/devel_propbank.open.112508_182555.log
F1 scores
              Global    : 0.796,0.810,0.811,0.811,0.812
          frameLabel    : 0.796,0.810,0.811,0.811,0.812

+ No global, or maybe yes, something weird going
bin/results.py results/devel_propbank.open.112508_182555.log
F1 scores
              Global    : 0.796,0.810,0.811,0.811,0.812
          frameLabel    : 0.796,0.810,0.811,0.811,0.812
+ No global again, something weird going 
bin/results.py results/devel_propbank.open.112508_194919.log
F1 scores
              Global    : 0.796,0.810,0.811,0.811,0.812
          frameLabel    : 0.796,0.810,0.811,0.811,0.812


+ Two rules
::
  bin/results.py results/devel_propbank.open.112508_223504.log
  F1 scores
              Global    : 0.834,0.842,0.841,0.841,0.841
          frameLabel    : 0.834,0.842,0.841,0.841,0.841

Eval 3
::
  bin/results.py results/devel_propbank.open.112508_223504.test.log
  F1 scores
              Global    : 0.855,0.673
          frameLabel    : 0.855,0.673

Final eval
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (10284 + 4521) / (13021 + 5321) * 100 = 80.72 %
  Labeled recall:             (10284 + 4521) / (14269 + 5260) * 100 = 75.81 %
  Labeled F1:                 78.19 
  Unlabeled precision:        (11757 + 5109) / (13021 + 5321) * 100 = 91.95 %
  Unlabeled recall:           (11757 + 5109) / (14269 + 5260) * 100 = 86.36 %
  Unlabeled F1:               89.07 
 
Brown
::
  head -13  data/conll05/test_brown_propbank.s3.112508_223504.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1335 + 555) / (1986 + 846) * 100 = 66.74 %
  Labeled recall:             (1335 + 555) / (2210 + 804) * 100 = 62.71 %
  Labeled F1:                 64.66 
  Unlabeled precision:        (1696 + 760) / (1986 + 846) * 100 = 86.72 %
  Unlabeled recall:           (1696 + 760) / (2210 + 804) * 100 = 81.49 %
  Unlabeled F1:               84.02 


+ bottm-up for the pipeline
::
  bin/results.py results/devel_propbank.open.120408_224003.log
  F1 scores
            hasLabel    : 0.867,0.871,0.869,0.869,0.868
              Global    : 0.824,0.831,0.830,0.831,0.831
                role    : 0.721,0.737,0.736,0.739,0.740
          isArgument    : 0.888,0.891,0.889,0.890,0.889

Eval(5)
::
  bin/results.py results/devel_propbank.open.120408_224003.test.log
  F1 scores
            hasLabel    : 0.877,0.835
              Global    : 0.848,0.780
                role    : 0.769,0.651
          isArgument    : 0.900,0.857

Stage 3
::
  bin/results.py results/devel_propbank.open.120508_192436.log
  F1 scores
              Global    : 0.834,0.842,0.841,0.841,0.841
          frameLabel    : 0.834,0.842,0.841,0.841,0.841

Eval(2)
::
  bin/results.py results/devel_propbank.open.120508_192436.test.log
  F1 scores
              Global    : 0.854,0.667
          frameLabel    : 0.854,0.667

Final eval
WSJ
::
  head -13 results/test_wsj_propbank.s3.120508_192436.2.eval
  SEMANTIC SCORES: 
  Labeled precision:          (10131 + 4516) / (12087 + 5321) * 100 = 84.14 %
  Labeled recall:             (10131 + 4516) / (14269 + 5260) * 100 = 75.00 %
  Labeled F1:                 79.31 
  Unlabeled precision:        (11040 + 5109) / (12087 + 5321) * 100 = 92.77 %
  Unlabeled recall:           (11040 + 5109) / (14269 + 5260) * 100 = 82.69 %
  Unlabeled F1:               87.44 
  

Brown
::
  head -13 results/test_brown_propbank.s3.120508_192436.2.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1291 + 550) / (1790 + 846) * 100 = 69.84 %
  Labeled recall:             (1291 + 550) / (2210 + 804) * 100 = 61.08 %
  Labeled F1:                 65.17 
  Unlabeled precision:        (1556 + 760) / (1790 + 846) * 100 = 87.86 %
  Unlabeled recall:           (1556 + 760) / (2210 + 804) * 100 = 76.84 %
  Unlabeled F1:               81.98 

+ There was a mistake with the training data for stage 3, reruning the stage 3
::
  bin/results.py results/devel_propbank.open.120808_142936.log
  F1 scores
              Global    : 0.832,0.837,0.838,0.837,0.836
          frameLabel    : 0.832,0.837,0.838,0.837,0.836

Eval (3)
::
  bin/results.py results/devel_propbank.open.120808_142936.test.log
  F1 scores
              Global    : 0.852,0.669
          frameLabel    : 0.852,0.669

Final eval
WSJ
:: 
  head -13 results/test_wsj_propbank.s3.120808_142936.3.eval
  SEMANTIC SCORES:
  Labeled precision:          (10131 + 4510) / (12087 + 5321) * 100 = 84.11 %
  Labeled recall:             (10131 + 4510) / (14269 + 5260) * 100 = 74.97 %
  Labeled F1:                 79.28
  Unlabeled precision:        (11040 + 5109) / (12087 + 5321) * 100 = 92.77 %
  Unlabeled recall:           (11040 + 5109) / (14269 + 5260) * 100 = 82.69 %
  Unlabeled F1:               87.44


Brown
::
  head -13 results/test_brown_propbank.s3.120808_142936.3.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1291 + 552) / (1790 + 846) * 100 = 69.92 %
  Labeled recall:             (1291 + 552) / (2210 + 804) * 100 = 61.15 %
  Labeled F1:                 65.24 
  Unlabeled precision:        (1556 + 760) / (1790 + 846) * 100 = 87.86 %
  Unlabeled recall:           (1556 + 760) / (2210 + 804) * 100 = 76.84 %
  Unlabeled F1:               81.98 

+ pipeline/bottom-up without isArg
Stage 2
::
  bin/results.py results/devel_propbank.open.120708_190242.log 
  F1 scores
            hasLabel    : 0.859,0.862,0.863,0.864,0.862
              Global    : 0.788,0.796,0.798,0.800,0.799
                role    : 0.718,0.729,0.733,0.737,0.736


Eval(4)
::
  bin/results.py results/devel_propbank.open.120708_190242.test.log
  F1 scores
            hasLabel    : 0.871,0.819
              Global    : 0.819,0.733
                role    : 0.767,0.645


Stage 3
::
  bin/results.py results/devel_propbank.open.120808_020636.log 
  F1 scores
              Global    : 0.831,0.835,0.835,0.837,0.837
          frameLabel    : 0.831,0.835,0.835,0.837,0.837

Eval (4)
::
  bin/results.py results/devel_propbank.open.120808_020636.test.log
  F1 scores
              Global    : 0.848,0.660
          frameLabel    : 0.848,0.660

Final eval
WSJ
::
  head -13  data/conll05/test_wsj_propbank.s3.120708_231441.4.eval
  SEMANTIC SCORES: 
  Labeled precision:          (10067 + 4486) / (11997 + 5321) * 100 = 84.03 %
  Labeled recall:             (10067 + 4486) / (14269 + 5260) * 100 = 74.52 %
  Labeled F1:                 78.99 
  Unlabeled precision:        (10941 + 5109) / (11997 + 5321) * 100 = 92.68 %
  Unlabeled recall:           (10941 + 5109) / (14269 + 5260) * 100 = 82.19 %
  Unlabeled F1:               87.12 
Brown
::
  head -13  data/conll05/test_brown_propbank.s3.120708_231441.4.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1265 + 542) / (1744 + 846) * 100 = 69.77 %
  Labeled recall:             (1265 + 542) / (2210 + 804) * 100 = 59.95 %
  Labeled F1:                 64.49 
  Unlabeled precision:        (1504 + 760) / (1744 + 846) * 100 = 87.41 %
  Unlabeled recall:           (1504 + 760) / (2210 + 804) * 100 = 75.12 %
  Unlabeled F1:               80.80 



+ To be able to compare I run full model
::
  bin/results.py  results/devel_propbank.open.112408_172158.log
  F1 scores
          isArgument    : 0.893,0.896,0.895,0.895,0.895
              Global    : 0.837,0.844,0.845,0.846,0.846
                role    : 0.703,0.719,0.724,0.725,0.727
         isPredicate    : 0.957,0.958,0.958,0.958,0.958
            hasLabel    : 0.865,0.869,0.869,0.869,0.870
          frameLabel    : 0.859,0.869,0.867,0.870,0.871

Eval 5
:: 
   bin/results.py results/devel_propbank.open.112408_172158.test.log 
   F1 scores
          isArgument    : 0.905,0.868
              Global    : 0.859,0.793
                role    : 0.752,0.646
         isPredicate    : 0.965,0.923
            hasLabel    : 0.879,0.836
          frameLabel    : 0.877,0.763

WSJ
::
  head -13 data/conll05/test_wsj_propbank.112408_172158.5.eval
  SEMANTIC SCORES: 
  Labeled precision:          (10372 + 4617) / (13334 + 5268) * 100 = 80.58 %
  Labeled recall:             (10372 + 4617) / (14269 + 5260) * 100 = 76.75 %
  Labeled F1:                 78.62 
  Unlabeled precision:        (11947 + 5079) / (13334 + 5268) * 100 = 91.53 %
  Unlabeled recall:           (11947 + 5079) / (14269 + 5260) * 100 = 87.18 %
  Unlabeled F1:               89.30 

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1367 + 613) / (2058 + 804) * 100 = 69.18 %
  Labeled recall:             (1367 + 613) / (2210 + 804) * 100 = 65.69 %
  Labeled F1:                 67.39 
  Unlabeled precision:        (1736 + 742) / (2058 + 804) * 100 = 86.58 %
  Unlabeled recall:           (1736 + 742) / (2210 + 804) * 100 = 82.22 %
  Unlabeled F1:               84.34 


No isArg
::
  bin/results.py results/devel_propbank.open.112408_225816.log
  F1 scores
         isPredicate    : 0.955,0.954,0.954,0.953,0.953
            hasLabel    : 0.861,0.863,0.863,0.863,0.862
              Global    : 0.818,0.824,0.826,0.826,0.827
                role    : 0.705,0.719,0.722,0.724,0.725
          frameLabel    : 0.859,0.866,0.867,0.868,0.873

+ Eval (5)
::
  bin/results.py results/devel_propbank.open.112408_225816.test.log 
  F1 scores
         isPredicate    : 0.963,0.910
            hasLabel    : 0.872,0.818
              Global    : 0.842,0.757
                role    : 0.752,0.638
          frameLabel    : 0.878,0.761

Final eval
WSJ
::
  head -13  data/conll05/test_wsj_propbank.112408_225816.5.eval

  SEMANTIC SCORES: 
  Labeled precision:          (10308 + 4615) / (13132 + 5257) * 100 = 81.15 %
  Labeled recall:             (10308 + 4615) / (14269 + 5260) * 100 = 76.41 %
  Labeled F1:                 78.71 
  Unlabeled precision:        (11765 + 5064) / (13132 + 5257) * 100 = 91.52 %
  Unlabeled recall:           (11765 + 5064) / (14269 + 5260) * 100 = 86.17 %
  Unlabeled F1:               88.77 

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1324 + 609) / (1973 + 797) * 100 = 69.78 %
  Labeled recall:             (1324 + 609) / (2210 + 804) * 100 = 64.13 %
  Labeled F1:                 66.84 
  Unlabeled precision:        (1665 + 728) / (1973 + 797) * 100 = 86.39 %
  Unlabeled recall:           (1665 + 728) / (2210 + 804) * 100 = 79.40 %
  Unlabeled F1:               82.75 


+ Two rules
::
  bin/results.py results/devel_propbank.open.112508_235756.log
  F1 scores
          isArgument    : 0.894,0.895,0.895,0.895,0.895
              Global    : 0.838,0.843,0.845,0.846,0.847
                role    : 0.703,0.713,0.720,0.724,0.727
         isPredicate    : 0.958,0.959,0.958,0.958,0.958
            hasLabel    : 0.865,0.868,0.869,0.869,0.869
          frameLabel    : 0.870,0.874,0.878,0.877,0.878

Final Eval(5)
::
  bin/results.py results/devel_propbank.open.112508_235756.test.log 
  F1 scores
          isArgument    : 0.906,0.869
              Global    : 0.861,0.795
                role    : 0.755,0.646
         isPredicate    : 0.965,0.923
            hasLabel    : 0.879,0.838
          frameLabel    : 0.885,0.771

Final eval
::
  head -13 data/conll05/test_wsj_propbank.112508_235756.eval
  SEMANTIC SCORES: 
  Labeled precision:          (10423 + 4664) / (13348 + 5273) * 100 = 81.02 %
  Labeled recall:             (10423 + 4664) / (14269 + 5260) * 100 = 77.25 %
  Labeled F1:                 79.09 
  Unlabeled precision:        (11960 + 5080) / (13348 + 5273) * 100 = 91.51 %
  Unlabeled recall:           (11960 + 5080) / (14269 + 5260) * 100 = 87.25 %
  Unlabeled F1:               89.33 
 

Brown
::
  head -13 data/conll05/test_brown_propbank.112508_235756.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1369 + 621) / (2063 + 807) * 100 = 69.34 %
  Labeled recall:             (1369 + 621) / (2210 + 804) * 100 = 66.03 %
  Labeled F1:                 67.64 
  Unlabeled precision:        (1742 + 743) / (2063 + 807) * 100 = 86.59 %
  Unlabeled recall:           (1742 + 743) / (2210 + 804) * 100 = 82.45 %
  Unlabeled F1:               84.47 

+ Testing no CPI
bin/results.py results/devel_propbank.open.112508_235756.nocpi.log
F1 scores
          isArgument    : 0.906,0.869
              Global    : 0.862,0.795
                role    : 0.757,0.648
         isPredicate    : 0.964,0.923
            hasLabel    : 0.879,0.838
          frameLabel    : 0.886,0.771


+ One rule [weight w_ip_fl_role]
::
  bin/results.py results/devel_propbank.open.112608_145555.log
  F1 scores
          isArgument    : 0.894,0.896,0.896,0.896,0.895
              Global    : 0.839,0.844,0.846,0.847,0.846
                role    : 0.706,0.717,0.724,0.726,0.726
         isPredicate    : 0.958,0.958,0.960,0.959,0.958
            hasLabel    : 0.867,0.870,0.871,0.871,0.869
          frameLabel    : 0.864,0.867,0.867,0.869,0.867

+ No isArg again for version with two global formulae
::
  bin/results.py results/devel_propbank.open.120308_222030.log
  F1 scores
         isPredicate    : 0.953,0.954,0.955,0.954,0.953
            hasLabel    : 0.860,0.861,0.863,0.863,0.863
              Global    : 0.818,0.823,0.825,0.827,0.827
                role    : 0.705,0.715,0.719,0.723,0.725
          frameLabel    : 0.866,0.873,0.873,0.874,0.873
 
Eval (4)
::
  bin/results.py results/devel_propbank.open.120308_222030.test.log
  F1 scores
         isPredicate    : 0.963,0.914
            hasLabel    : 0.873,0.820
              Global    : 0.843,0.758
                role    : 0.753,0.635
          frameLabel    : 0.881,0.761


Final eval:
WSJ
::
  SEMANTIC SCORES: 
  Labeled precision:          (10325 + 0) / (13145 + 5256) * 100 = 78.55 %
  Labeled recall:             (10325 + 0) / (14269 + 5260) * 100 = 72.36 %
  Labeled F1:                 75.33 
  Unlabeled precision:        (11783 + 0) / (13145 + 5256) * 100 = 64.03 %
  Unlabeled recall:           (11783 + 0) / (14269 + 5260) * 100 = 60.34 %
  Unlabeled F1:               62.13 


Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1323 + 0) / (1988 + 803) * 100 = 66.55 %
  Labeled recall:             (1323 + 0) / (2210 + 804) * 100 = 59.86 %
  Labeled F1:                 63.03 
  Unlabeled precision:        (1675 + 0) / (1988 + 803) * 100 = 60.01 %
  Unlabeled recall:           (1675 + 0) / (2210 + 804) * 100 = 55.57 %
  Unlabeled F1:               57.71 


+ Bottom up
::
  bin/results.py results/devel_propbank.open.112708_202510.log
  F1 scores
          isArgument    : 0.893,0.895,0.895,0.894,0.894
              Global    : 0.847,0.852,0.853,0.854,0.854
                role    : 0.728,0.738,0.743,0.746,0.747
         isPredicate    : 0.958,0.960,0.960,0.960,0.959
            hasLabel    : 0.867,0.870,0.869,0.870,0.869
          frameLabel    : 0.872,0.878,0.885,0.887,0.883

Eval(5)
::
  bin/results.py results/devel_propbank.open.112708_202510.test.log
  F1 scores
          isArgument    : 0.904,0.868
              Global    : 0.866,0.803
                role    : 0.773,0.664
         isPredicate    : 0.965,0.926
            hasLabel    : 0.876,0.839
          frameLabel    : 0.888,0.783

 
Final Eval
WSJ
::
  head -13 results/test_wsj_propbank.112708_202510.eval
  SEMANTIC SCORES:
  Labeled precision:          (10298 + 4594) / (12366 + 5262) * 100 = 84.48 %
  Labeled recall:             (10298 + 4594) / (14269 + 5260) * 100 = 76.26 %
  Labeled F1:                 80.16
  Unlabeled precision:        (11218 + 5075) / (12366 + 5262) * 100 = 92.43 %
  Unlabeled recall:           (11218 + 5075) / (14269 + 5260) * 100 = 83.43 %
  Unlabeled F1:               87.70

Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1334 + 595) / (1844 + 814) * 100 = 72.57 %
  Labeled recall:             (1334 + 595) / (2210 + 804) * 100 = 64.00 %
  Labeled F1:                 68.02 
  Unlabeled precision:        (1587 + 749) / (1844 + 814) * 100 = 87.89 %
  Unlabeled recall:           (1587 + 749) / (2210 + 804) * 100 = 77.50 %
  Unlabeled F1:               82.37 

+ Turning off CPI for testing
bin/results.py results/devel_propbank.open.112708_202510.nocpi.log
F1 scores
          isArgument    : 0.904,0.866
              Global    : 0.867,0.800
                role    : 0.775,0.662
         isPredicate    : 0.965,0.925
            hasLabel    : 0.877,0.836
          frameLabel    : 0.890,0.775




+ Turning off CPI for training 
::
  bin/results.py results/devel_propbank.open.112808_021447.log
  F1 scores
          isArgument    : 0.893,0.895,0.895,0.895,0.895
              Global    : 0.846,0.852,0.854,0.855,0.855
                role    : 0.726,0.738,0.743,0.746,0.748
         isPredicate    : 0.959,0.959,0.960,0.960,0.960
            hasLabel    : 0.866,0.870,0.871,0.871,0.872
          frameLabel    : 0.871,0.880,0.884,0.882,0.880

Previous result is wrong! I did some silly thing.
+ Now turning off CPI
::
  bin/results.py results/devel_propbank.open.112808_154705.log
  F1 scores
          isArgument    : 0.894,0.896,0.895,0.895,0.893
              Global    : 0.840,0.845,0.847,0.847,0.846
                role    : 0.708,0.720,0.726,0.727,0.727
         isPredicate    : 0.959,0.958,0.958,0.957,0.957
            hasLabel    : 0.867,0.869,0.870,0.868,0.867
          frameLabel    : 0.873,0.878,0.880,0.879,0.879



bin/results.py results/devel_propbank.open.112808_021447.test.log
F1 scores
          isArgument    : 0.905,0.865
              Global    : 0.867,0.800
                role    : 0.775,0.661
         isPredicate    : 0.964,0.925
            hasLabel    : 0.877,0.836
          frameLabel    : 0.886,0.778

Final eval

WSJ
::
  head -13 results/test_wsj_propbank.112808_021447.eval

  SEMANTIC SCORES: 
  Labeled precision:          (10323 + 4546) / (12384 + 5267) * 100 = 84.24 %
  Labeled recall:             (10323 + 4546) / (14269 + 5260) * 100 = 76.14 %
  Labeled F1:                 79.98 
  Unlabeled precision:        (11242 + 5076) / (12384 + 5267) * 100 = 92.45 %
  Unlabeled recall:           (11242 + 5076) / (14269 + 5260) * 100 = 83.56 %
  Unlabeled F1:               87.78 

Brown
::
  head -13 results/test_brown_propbank.112808_021447.eval

  SEMANTIC SCORES: 
  Labeled precision:          (1324 + 600) / (1833 + 812) * 100 = 72.74 %
  Labeled recall:             (1324 + 600) / (2210 + 804) * 100 = 63.84 %
  Labeled F1:                 68.00 
  Unlabeled precision:        (1576 + 747) / (1833 + 812) * 100 = 87.83 %
  Unlabeled recall:           (1576 + 747) / (2210 + 804) * 100 = 77.07 %
  Unlabeled F1:               82.10 

 
+ Bottom-up without isArgument
::
 bin/results.py results/devel_propbank.open.112908_202731.log
 F1 scores
         isPredicate    : 0.953,0.955,0.955,0.955,0.954
            hasLabel    : 0.859,0.863,0.863,0.863,0.862
              Global    : 0.826,0.834,0.835,0.836,0.836
                role    : 0.725,0.739,0.742,0.743,0.745
          frameLabel    : 0.870,0.875,0.877,0.876,0.876

Eval (5)
::
  bin/results.py results/devel_propbank.open.112908_202731.test.log
  F1 scores
         isPredicate    : 0.963,0.914
            hasLabel    : 0.871,0.825
              Global    : 0.849,0.767
                role    : 0.769,0.652
          frameLabel    : 0.883,0.761

Finale eval
WSJ
::
  head -13  results/test_wsj_propbank.112908_202731.eval 
  SYNTACTIC SCORES:
  Labeled   attachment score: 0 / 56618 * 100 = 0.00 %
  Unlabeled attachment score: 0 / 56618 * 100 = 0.00 %
  Label accuracy score:       0 / 56618 * 100 = 0.00 %

  SEMANTIC SCORES: 
  Labeled precision:          (10218 + 4495) / (12293 + 5252) * 100 = 83.86 %
  Labeled recall:             (10218 + 4495) / (14269 + 5260) * 100 = 75.34 %
  Labeled F1:                 79.37 
  Unlabeled precision:        (11127 + 5061) / (12293 + 5252) * 100 = 92.27 %
  Unlabeled recall:           (11127 + 5061) / (14269 + 5260) * 100 = 82.89 %
  Unlabeled F1:               87.33 


Brown
::
  head -13  results/test_brown_propbank.112908_202731.eval
  SEMANTIC SCORES: 
  Labeled precision:          (1298 + 578) / (1806 + 805) * 100 = 71.85 %
  Labeled recall:             (1298 + 578) / (2210 + 804) * 100 = 62.24 %
  Labeled F1:                 66.70 
  Unlabeled precision:        (1539 + 735) / (1806 + 805) * 100 = 87.09 %
  Unlabeled recall:           (1539 + 735) / (2210 + 804) * 100 = 75.45 %
  Unlabeled F1:               80.85 



Measuring times
---------------
Training
::
  grep Time results/devel_propbank.open.112808_154705.log | linex 1 4-14 | sed 's/Time://' | sed 's/m//' | awk -F\  '{ SUM += $1} END { print SUM/60 }'

Testing
:: 
  grep Time results/devel_propbank.open.112808_154705.test.log

Dealing with differences among the corpora
-----------------------------------

There are two sources of information:
- Mihai software (who kindly give us it's software):
  * data/conll05/*_propbank.output
  * Conll 2008 format
  * PropBank only (Conll 2008/shared task had PropBank and NomBank)
  * We believe dependencies were PenTree bank gold standard.
- Sebastian Conll-2005
  * data/conll05/*-set
  * Conll 2005 format
  * PropBank
  * Constituencies parses

We wanted:
  * Conll 2008 format
  * PropBank 
  * Dependencies but from Charniack Parses

To reach this we use:
  1. Extact the parses from Seb's files::

     bin/extractPennTreeBank.py 
     
  2. Transform the parses to conll06 format
  (http://nlp.cs.lth.se/pennconverter/)::

     bin/java -jar ../app/pennconverter.jar

  3. To prepare the data run::

     ./scripts/generate_charniakparses

  4. To generate the beast file run::

    ./scripts/translatecorpora2beast_eacl_possiblePred_chk

From this process there are differences among our beast corpora and Sebastian's conll05 which is a problem for evaluating. Since Mihai is missing some sentences in training and testing wsj. For this reason we modify the original test-set-wsj Seb conll05 file to no include these sentences. These sentences are the ones which start with:
    1. The well flowed
    2. Mrs. Crump said
A copy of the original file is in: test-set-wsj.old

The beast files are in:
  * data/conll05/*_propbank.[atoms/types.pml]




Comparing we don't lost anything
================================

We need to verify we don't lost anything during the transformation of data, for
this we check:
:: 
     [dendrite]s0343487: bin/beast2conll05.py -p
     data/conll05/devel_propbank.atoms > tmp
     [dendrite]s0343487: head tmp
     The     _       DT      _       _       _       _       _       (A1*
     economy _       NN      _       _       _       _       _       *
     's      _       POS     _       _       _       _       _       *
     temperature     _       NN      _       _       _       _       _       *)
     will    _       MD      _       _       _       _       _       (AM-MOD*)
     be      _       VB      _       _       _       _       _       *
     taken   _       VBN     _       _       _       01      take    (V*)    *
     from    _       IN      _       _       _       _       _       (A2*
     several _       JJ      _       _       _       _       _       *
     vantage _       NN      _       _       _       _       _       *)
     [dendrite]s0343487: grep "^\s*$" data/conll05st-release/dev-set | wc
     1346       0    9422
     [dendrite]s0343487: grep "^\s*$" tmp | wc
     1346       0    1346
     [dendrite]../app/srl-eval.pl  data/conll05st-release/dev-set tmp
     
There are errors, the head dependencies don't span the right argument. Work to do analyse this. For right now we will stick with 2008 evaluation. Conclusion: It's not possible to compare conll08 with conll05



EVALUATING charniak dep parsing
===============================
::
    nice bin/conll082beast.py -v -H -G  -d /dev/null --output_mst -8 data/conll05/test_wsj_propbank.gs.conll08 -o data/conll05/test_wsj_propbank.gs.atoms -M data/conll05st-release/test-set-wsj.penn.gs data/conll05/test_wsj_propbank.output
    ../app/eval08.pl -g data/conll05/test_wsj_propbank.gs.conll08 -s data/conll05/test_wsj_propbank.conll08

Output::
  SYNTACTIC SCORES:
  Labeled   attachment score: 40795 / 56618 * 100 = 72.05 %
  Unlabeled attachment score: 49217 / 56618 * 100 = 86.93 %
  Label accuracy score:       43555 / 56618 * 100 = 76.93 %


Scores for three fisrt systems (conll08)

Johansson
  SYNTACTIC SCORES:
  Labeled   attachment score: 51985 / 57676 * 100 = 90.13 %
  Unlabeled attachment score: 53319 / 57676 * 100 = 92.45 %
  Label accuracy score:       54170 / 57676 * 100 = 93.92 %


Che
  Labeled   attachment score: 50471 / 57676 * 100 = 87.51 %
  Unlabeled attachment score: 51925 / 57676 * 100 = 90.03 %
  Label accuracy score:       53211 / 57676 * 100 = 92.26 %


Ciaramita
  SYNTACTIC SCORES:
  Labeled   attachment score: 50448 / 57676 * 100 = 87.47 %
  Unlabeled attachment score: 52023 / 57676 * 100 = 90.20 %
  Label accuracy score:       53137 / 57676 * 100 = 92.13 %




Switching to Johansson 2008 features
====================================

Role features
-------------

Charniak parses
---------------
::
    bin/results.py results/devel_propbank.open.102108_150453.log
    F1 scores
              Global    : 0.709,0.727,0.730,0.732,0.734
                role    : 0.709,0.727,0.730,0.732,0.734

After fixing some rules
::
    bin/results.py results/devel_propbank.open.102308_235927.log
    F1 scores
              Global    : 0.718,0.734,0.738,0.742,0.741
                role    : 0.718,0.734,0.738,0.742,0.741

After including the childDepSet rule (Note it's not really a set of child)
::
    bin/results.py results/devel_propbank.open.102408_163359.log
    F1 scores
              Global    : 0.715,0.730,0.734,0.739,0.740
                role    : 0.715,0.730,0.734,0.739,0.740

Fixing childDepSet to be a set (This have a bug, it wasn't generating the right corpus)
::
   bin/results.py results/devel_propbank.open.102508_184406.log
   F1 scores
              Global    : 0.718,0.734,0.738,0.742,0.741
                role    : 0.718,0.734,0.738,0.742,0.741

Adding hasLabel (removing hasSubj)
::
  bin/results.py results/devel_propbank.open.102608_151750.log
  F1 scores
            hasLabel    : 0.866,0.869,0.871,0.871,0.871
              Global    : 0.788,0.796,0.799,0.800,0.802
                role    : 0.712,0.725,0.730,0.731,0.734

Addind old hasSubj
::
  bin/results.py results/devel_propbank.open.102608_220657.log
  F1 scores
            hasLabel    : 0.867,0.870,0.871,0.871,0.872
              Global    : 0.790,0.796,0.799,0.800,0.801
                role    : 0.716,0.725,0.729,0.731,0.732

Fixing hasSubj to use the verb chains in role
::
  bin/results.py results/devel_propbank.open.102708_172010.log
  F1 scores
            hasLabel    : 0.867,0.871,0.871,0.872,0.873
              Global    : 0.790,0.797,0.799,0.801,0.803
                role    : 0.715,0.725,0.729,0.731,0.735
   
Adding hasSubj and controller to hasLabel
::
  bin/results.py results/devel_propbank.open.102808_003417.log
  F1 scores
            hasLabel    : 0.866,0.869,0.871,0.871,0.871
              Global    : 0.789,0.797,0.800,0.800,0.801
                role    : 0.713,0.727,0.731,0.731,0.732

Fixing childDepSet for hasLabel, and lemma->word, and index weight of hasSub and controller with lemma of argument
::
  bin/results.py results/devel_propbank.open.102808_153520.log
  F1 scores
            hasLabel    : 0.865,0.869,0.872,0.873,0.873
              Global    : 0.787,0.795,0.799,0.802,0.802
                role    : 0.711,0.723,0.729,0.732,0.734

Adding isArgument predicate
::
  bin/results.py results/devel_propbank.open.102808_213149.log
  F1 scores
            hasLabel    : 0.862,0.868,0.869,0.871,0.871
              Global    : 0.816,0.824,0.826,0.829,0.830
                role    : 0.709,0.724,0.729,0.732,0.735
          isArgument    : 0.885,0.889,0.890,0.891,0.891

Adding isPredicate [Changing two local formulae to global rules: w_r_predLemmaSense and w_r_predLemma ]
Note: the hasLabel global are not included yet, and the weights are not contrainstraint.
::
  bin/results.py results/devel_propbank.open.103008_142423.log
  F1 scores
         isPredicate    : 0.970,0.973,0.974,0.974,0.974
            hasLabel    : 0.861,0.864,0.865,0.867,0.867
              Global    : 0.833,0.837,0.840,0.841,0.842
                role    : 0.705,0.715,0.720,0.725,0.725
          isArgument    : 0.885,0.886,0.886,0.886,0.886

Making weights for global only negative
::
  bin/results.py results/devel_propbank.open.103008_214101.log
  F1 scores
         isPredicate    : 0.971,0.972,0.974,0.974,0.974
            hasLabel    : 0.861,0.866,0.868,0.869,0.868
              Global    : 0.832,0.838,0.841,0.843,0.842
                role    : 0.702,0.716,0.722,0.726,0.725
          isArgument    : 0.884,0.886,0.887,0.888,0.888

having the same order the order of global
::
  bin/results.py results/devel_propbank.open.103108_111206.log
  F1 scores
         isPredicate    : 0.972,0.973,0.973,0.974,0.974
            hasLabel    : 0.862,0.866,0.868,0.868,0.868
              Global    : 0.831,0.838,0.840,0.841,0.842
                role    : 0.699,0.713,0.720,0.722,0.724
          isArgument    : 0.886,0.887,0.888,0.887,0.888

Interchanging the order of global
::
  bin/results.py results/devel_propbank.open.103108_204143.log
  F1 scores
         isPredicate    : 0.972,0.973,0.973,0.974,0.974
            hasLabel    : 0.863,0.867,0.868,0.867,0.867
              Global    : 0.833,0.839,0.840,0.840,0.841
                role    : 0.703,0.716,0.719,0.720,0.723
          isArgument    : 0.885,0.888,0.888,0.888,0.888

Adding global for hasLabel (order lower than role)
::
  Too slow

Adding global for hasLabel (order greater than role)
::
  Too slow


No using observable hidden variables
------------------------------------

Adding frameLabel (No collect all)
::
  bin/results.py  results/devel_propbank.open.110208_033717.log
  F1 scores
          isArgument    : 0.776,0.777,0.781,0.782,0.781
              Global    : 0.735,0.742,0.750,0.753,0.754
                role    : 0.624,0.635,0.645,0.649,0.652
         isPredicate    : 0.872,0.880,0.890,0.892,0.893
            hasLabel    : 0.750,0.754,0.760,0.761,0.761
          frameLabel    : 0.745,0.761,0.778,0.786,0.787

Changing the global order
::
  bin/results.py results/devel_propbank.open.110208_220623.log
  F1 scores
          isArgument    : 0.779,0.778,0.781,0.778,0.777
              Global    : 0.739,0.745,0.749,0.748,0.750
                role    : 0.628,0.638,0.644,0.645,0.647
         isPredicate    : 0.876,0.883,0.886,0.888,0.891
            hasLabel    : 0.754,0.757,0.760,0.758,0.758
          frameLabel    : 0.751,0.767,0.773,0.775,0.777

Only isPredicate
::
  bin/results.py results/devel_propbank.open.110308_134659.log
  F1 scores
         isPredicate    : 0.913,0.923,0.927,0.927,0.927
              Global    : 0.913,0.923,0.927,0.927,0.927

Adding Lema 
::
  bin/results.py results/devel_propbank.open.110308_153911.log
  F1 scores
         isPredicate    : 0.912,0.920,0.926,0.927,0.927
              Global    : 0.912,0.920,0.926,0.927,0.927


Only framelabel
::
  bin/results.py results/devel_propbank.open.110308_190427.log
  F1 scores
              Global    : 0.814,0.820,0.821,0.821,0.821
          frameLabel    : 0.814,0.820,0.821,0.821,0.821

Only isArgument
::
  bin/results.py results/devel_propbank.open.110308_180333.log
  F1 scores
              Global    : 0.850,0.852,0.851,0.849,0.846
          isArgument    : 0.850,0.852,0.851,0.849,0.846

isPredicate and framelabel
::
  bin/results.py results/devel_propbank.open.110308_183806.log
  F1 scores
         isPredicate    : 0.911,0.923,0.925,0.926,0.929
              Global    : 0.864,0.878,0.880,0.880,0.882
          frameLabel    : 0.817,0.832,0.834,0.833,0.835
    

isPredicate and isArgument and frameLabel
::
  bin/results.py results/devel_propbank.open.110308_192404.log
  F1 scores
         isPredicate    : 0.900,0.913,0.920,0.926,0.928
              Global    : 0.850,0.856,0.859,0.863,0.864
          frameLabel    : 0.801,0.816,0.823,0.832,0.836
          isArgument    : 0.849,0.849,0.849,0.849,0.848
 
isPredicate and isArgument and framelabel and hasLabel (no links between isPredicate and isArguemnt with hasLabel)
::
  bin/results.py results/devel_propbank.open.110308_205802.log
  F1 scores
         isPredicate    : 0.905,0.915,0.922,0.927,0.928
            hasLabel    : 0.774,0.786,0.793,0.797,0.800
              Global    : 0.824,0.832,0.836,0.839,0.841
          frameLabel    : 0.806,0.820,0.828,0.833,0.836
          isArgument    : 0.852,0.852,0.851,0.851,0.851
  
  
isPredicate and isArgument and framelabel and hasLabel (adding links between isPredicate and isArguemnt with hasLabel)
::
  bin/results.py results/devel_propbank.open.110308_214410.log
  F1 scores
         isPredicate    : 0.932,0.937,0.940,0.942,0.941
            hasLabel    : 0.804,0.809,0.812,0.814,0.813
              Global    : 0.846,0.852,0.854,0.855,0.854
          frameLabel    : 0.828,0.840,0.843,0.845,0.846
          isArgument    : 0.864,0.867,0.868,0.868,0.866

isPredicate and isArgument and framelabel and hasLabel (global isArgument)
::
  bin/results.py results/devel_propbank.open.110308_224852.log
  F1 scores
         isPredicate    : 0.927,0.929,0.933,0.935,0.934
            hasLabel    : 0.800,0.806,0.808,0.811,0.810
              Global    : 0.837,0.843,0.846,0.848,0.848
          frameLabel    : 0.780,0.795,0.802,0.810,0.813
          isArgument    : 0.863,0.866,0.868,0.869,0.869

Commenting out w_iag_predSense
::
  bin/results.py results/devel_propbank.open.110408_015959.log
  F1 scores
         isPredicate    : 0.930,0.933,0.937,0.940,0.939
            hasLabel    : 0.803,0.808,0.811,0.813,0.813
              Global    : 0.844,0.849,0.852,0.854,0.855
          frameLabel    : 0.819,0.829,0.835,0.838,0.842
          isArgument    : 0.864,0.867,0.869,0.870,0.870

Adding role (no links between hasLabel and role)
::
  bin/results.py results/devel_propbank.open.110408_033042.log
  F1 scores
          isArgument    : 0.868,0.866,0.869,0.870,0.870
              Global    : 0.799,0.805,0.810,0.812,0.814
                role    : 0.664,0.681,0.689,0.695,0.699
         isPredicate    : 0.931,0.933,0.937,0.939,0.940
            hasLabel    : 0.805,0.808,0.812,0.814,0.815
          frameLabel    : 0.823,0.833,0.834,0.837,0.837

Adding links
::
  bin/results.py results/devel_propbank.open.110408_124931.log
  F1 scores
          isArgument    : 0.868,0.873,0.875,0.874,0.875
              Global    : 0.807,0.817,0.821,0.822,0.823
                role    : 0.675,0.691,0.697,0.702,0.702
         isPredicate    : 0.930,0.937,0.940,0.941,0.942
            hasLabel    : 0.834,0.842,0.845,0.846,0.846
          frameLabel    : 0.817,0.831,0.834,0.837,0.837

Adding global for role
::
  bin/results.py results/devel_propbank.open.110408_232451.log
  F1 scores
          isArgument    : 0.868,0.870,0.873,0.873,0.873
              Global    : 0.806,0.811,0.815,0.817,0.819
                role    : 0.671,0.681,0.690,0.692,0.695
         isPredicate    : 0.931,0.935,0.936,0.939,0.940
            hasLabel    : 0.833,0.836,0.841,0.840,0.843
          frameLabel    : 0.816,0.820,0.823,0.828,0.832

Adding lemma argument to global rules of role
::
  bin/results.py results/devel_propbank.open.110508_115710.log
  F1 scores
          isArgument    : 0.868,0.870,0.873,0.874,0.874
              Global    : 0.807,0.815,0.820,0.823,0.823
                role    : 0.675,0.692,0.700,0.704,0.704
         isPredicate    : 0.930,0.935,0.939,0.941,0.942
            hasLabel    : 0.834,0.840,0.844,0.846,0.846
          frameLabel    : 0.814,0.822,0.829,0.835,0.833

Evaluation
::
  F1 scores
          isArgument    : 0.892,0.849
              Global    : 0.842,0.756
                role    : 0.738,0.592
         isPredicate    : 0.953,0.900
            hasLabel    : 0.859,0.799
          frameLabel    : 0.839,0.706


WSJ
::
  head -13 results/test_wsj_propbank.110508_115710.5.conll08
  SEMANTIC SCORES: 
  Labeled precision:          (10152 + 4385) / (14269 + 5260) * 100 = 74.44 %
  Labeled recall:             (10152 + 4385) / (13225 + 5195) * 100 = 78.92 %
  Labeled F1:                 76.61 
  Unlabeled precision:        (11637 + 4983) / (14269 + 5260) * 100 = 85.10 %
  Unlabeled recall:           (11637 + 4983) / (13225 + 5195) * 100 = 90.23 %
  Unlabeled F1:               87.59 

Brown
::
  head -12 results/test_brown_propbank.110508_115710.5.conll08
  SEMANTIC SCORES: 
  Labeled precision:          (1228 + 551) / (2210 + 804) * 100 = 59.02 %
  Labeled recall:             (1228 + 551) / (1970 + 757) * 100 = 65.24 %
  Labeled F1:                 61.98 
  Unlabeled precision:        (1625 + 702) / (2210 + 804) * 100 = 77.21 %
  Unlabeled recall:           (1625 + 702) / (1970 + 757) * 100 = 85.33 %
  Unlabeled F1:               81.07

Adding old FrameLabel global
::
  bin/results.py results/devel_propbank.open.110508_224123.log
  F1 scores
          isArgument    : 0.867,0.872,0.874,0.873,0.874
              Global    : 0.807,0.816,0.820,0.821,0.823
                role    : 0.672,0.687,0.694,0.697,0.701
         isPredicate    : 0.929,0.934,0.937,0.938,0.939
            hasLabel    : 0.832,0.838,0.842,0.843,0.844
          frameLabel    : 0.833,0.841,0.847,0.848,0.849
 
    
CONLL-05 version (predicates known)
----------------
::
  bin/results.py results/devel_propbank.open.111608_031922.log
  F1 scores
            hasLabel    : 0.865,0.868,0.869,0.870,0.870
              Global    : 0.832,0.838,0.841,0.842,0.843
                role    : 0.714,0.729,0.734,0.736,0.738
          frameLabel    : 0.889,0.896,0.905,0.906,0.903
          isArgument    : 0.901,0.902,0.903,0.903,0.903

EVAL(5)
::
  bin/results.py results/devel_propbank.open.111608_031922.test.log
  F1 scores
            hasLabel    : 0.885,0.849
              Global    : 0.858,0.793
                role    : 0.768,0.637
          frameLabel    : 0.896,0.810
          isArgument    : 0.914,0.897

Final eval

WSJ
::
  head -13 results/test_wsj_propbank.111608_031922.5.eval
  SYNTACTIC SCORES:
  Labeled   attachment score: 0 / 56618 * 100 = 0.00 %
  Unlabeled attachment score: 0 / 56618 * 100 = 0.00 %
  Label accuracy score:       0 / 56618 * 100 = 0.00 %

  SEMANTIC SCORES:
  Labeled precision:          (10674 + 4713) / (13521 + 5260) * 100 = 81.93 %
  Labeled recall:             (10674 + 4713) / (14269 + 5260) * 100 = 78.79 %
  Labeled F1:                 80.33
  Unlabeled precision:        (12209 + 5260) / (13521 + 5260) * 100 = 93.01 %
  Unlabeled recall:           (12209 + 5260) / (14269 + 5260) * 100 = 89.45 %
  Unlabeled F1:               91.20
 

Brown
::
  head -13 results/test_brown_propbank.111608_031922.5.eval
  SEMANTIC SCORES:
  Labeled precision:          (1332 + 651) / (2009 + 805) * 100 = 70.47 %
  Labeled recall:             (1332 + 651) / (2210 + 804) * 100 = 65.79 %
  Labeled F1:                 68.05
  Unlabeled precision:        (1770 + 804) / (2009 + 805) * 100 = 91.47 %
  Unlabeled recall:           (1770 + 804) / (2210 + 804) * 100 = 85.40 %
  Unlabeled F1:               88.33




Gold dependencies
-----------------
::
    bin/results.py results/devel_propbank.open.102208_131639.log
    F1 scores
              Global    : 0.785,0.800,0.804,0.807,0.809
                role    : 0.785,0.800,0.804,0.807,0.809

Fixing some rules
::
    bin/results.py results/devel_propbank.open.102308_181043.log
    F1 scores
              Global    : 0.796,0.807,0.814,0.819,0.818
                role    : 0.796,0.807,0.814,0.819,0.818


Final models
::
  bin/results.py devel_propbank.open.110608_214114.log
  F1 scores
          isArgument    : 0.925,0.926,0.929,0.929,0.930
              Global    : 0.863,0.871,0.876,0.877,0.878
                role    : 0.755,0.768,0.777,0.781,0.783
         isPredicate    : 0.939,0.943,0.946,0.946,0.947
            hasLabel    : 0.903,0.911,0.915,0.915,0.916
          frameLabel    : 0.826,0.835,0.841,0.840,0.844

EVAL(5)
::
   bin/results.py devel_propbank.open.110608_214114.test.log
   F1 scores
          isArgument    : 0.942,0.901
              Global    : 0.897,0.820
                role    : 0.825,0.698
         isPredicate    : 0.957,0.910
            hasLabel    : 0.927,0.874
          frameLabel    : 0.848,0.720

WSJ
::
  head -13 results/test_wsj_propbank.110608_214114.5.conll
  SEMANTIC SCORES: 
  Labeled precision:          (11429 + 4432) / (14269 + 5260) * 100 = 81.22 %
  Labeled recall:             (11429 + 4432) / (13448 + 5196) * 100 = 85.07 %
  Labeled F1:                 83.10 
  Unlabeled precision:        (12652 + 5003) / (14269 + 5260) * 100 = 90.40 %
  Unlabeled recall:           (12652 + 5003) / (13448 + 5196) * 100 = 94.70 %
  Unlabeled F1:               92.50 
 
Brown
::
  head -13 results/test_brown_propbank.110608_214114.5.conll
  SEMANTIC SCORES: 
  Labeled precision:          (1467 + 565) / (2210 + 804) * 100 = 67.42 %
  Labeled recall:             (1467 + 565) / (2030 + 766) * 100 = 72.68 %
  Labeled F1:                 69.95 
  Unlabeled precision:        (1803 + 714) / (2210 + 804) * 100 = 83.51 %
  Unlabeled recall:           (1803 + 714) / (2030 + 766) * 100 = 90.02 %
  Unlabeled F1:               86.64 
  
CONLL-05 version (known predicate)
----------------------------------

::
  bin/results.py results/devel_propbank.open.111608_155622.log
  F1 scores
            hasLabel    : 0.935,0.939,0.939,0.940,0.941
              Global    : 0.894,0.901,0.903,0.905,0.906
                role    : 0.799,0.814,0.818,0.822,0.826
          frameLabel    : 0.890,0.897,0.902,0.904,0.904
          isArgument    : 0.957,0.959,0.960,0.960,0.961


Eval (5)

::
  bin/results.py results/devel_propbank.open.111608_155622.test.log
  F1 scores
            hasLabel    : 0.954,0.934
              Global    : 0.921,0.872
                role    : 0.855,0.755
          frameLabel    : 0.899,0.816
          isArgument    : 0.967,0.956


Final eval  

WSJ
::
  SEMANTIC SCORES:
  Labeled precision:          (11972 + 4729) / (13750 + 5260) * 100 = 87.85 %
  Labeled recall:             (11972 + 4729) / (14269 + 5260) * 100 = 85.52 %
  Labeled F1:                 86.67
  Unlabeled precision:        (13272 + 5260) / (13750 + 5260) * 100 = 97.49 %
  Unlabeled recall:           (13272 + 5260) / (14269 + 5260) * 100 = 94.89 %
  Unlabeled F1:               96.17


Brown
:: 
  SEMANTIC SCORES:
  Labeled precision:          (1605 + 656) / (2073 + 805) * 100 = 78.56 %
  Labeled recall:             (1605 + 656) / (2210 + 804) * 100 = 75.02 %
  Labeled F1:                 76.75
  Unlabeled precision:        (1978 + 804) / (2073 + 805) * 100 = 96.66 %
  Unlabeled recall:           (1978 + 804) / (2210 + 804) * 100 = 92.30 %
  Unlabeled F1:               94.43


Using J08 evaluation
WSJ
  SEMANTIC SCORES: 
  Labeled precision:          (11972 + 0) / (13750 + 5260) * 100 = 87.07 %
  Labeled recall:             (11972 + 0) / (14269 + 5260) * 100 = 83.90 %
  Labeled F1:                 85.46 
  Unlabeled precision:        (13272 + 0) / (13750 + 5260) * 100 = 69.82 %
  Unlabeled recall:           (13272 + 0) / (14269 + 5260) * 100 = 67.96 %
  Unlabeled F1:               68.88 


Brown
::
  SEMANTIC SCORES: 
  Labeled precision:          (1605 + 0) / (2073 + 805) * 100 = 77.42 %
  Labeled recall:             (1605 + 0) / (2210 + 804) * 100 = 72.62 %
  Labeled F1:                 74.95 
  Unlabeled precision:        (1978 + 0) / (2073 + 805) * 100 = 68.73 %
  Unlabeled recall:           (1978 + 0) / (2210 + 804) * 100 = 65.63 %
  Unlabeled F1:               67.14 


Support Chains
==============

Support indicates when a Nominal predicate has one argument which doesn't belong to the NP to which the nominal predicate belongs, this is a long distance dependency. Some of the phenomena which triggers this syn/sem phenomena depends on the presence of control/raising verbs, this verb is called support. Conll08 labels the support chains, which is the sequence of words and dependencies which go from the support verb to the nominal predicate. This is indicated by a "SU" in the proposional verb and in the column to for the nominal predicate with the long distance argument. The path of between them is the support chain.


Verb Chains
===========

Verb chains are labelled in the dependency trees with the label "VC". To recover them we only have to follow the "VC" heads until no more. For verbChainHasSubj we need to check that each of the children of head of the verb chain is subject relation. For the controllerHasObj we check that the head of verb chain is "OPRD" and then if one of which children is OBJ.  





    
     




     










 



