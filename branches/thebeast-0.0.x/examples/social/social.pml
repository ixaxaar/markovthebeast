/* Predicate definitions */

predicate gender: Person x Gender;
predicate friends: Person x Person;
predicate likes: Person x Movie;
predicate movieType: Movie x MovieType;

hidden: likes;

// Local formulae that implies that a person likes a movie with
// a weight that depends on the type of movie and gender of the person.
weight w_gender: Gender x MovieType -> Double;
factor: for Person p, Gender g, MovieType t, Movie m
  if gender(p,g) & movieType(m,t) add [likes(p,m)] * w_gender(g,t);

// Global formulae that imples that a person likes a movie if his friend likes
// the movie with a weight that depends on genders of both persons
weight w_friend: Gender x Gender -> Double+;
factor: for Person p1, Person p2, Gender g1, Gender g2, Movie m
  if gender(p1,g1) & gender(p2,g2) & movieType(m,_) & friends(p1,p2)
  add [likes(p1,m)=>likes(p2,m)] * w_friend(g1,g2);

