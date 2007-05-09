//1.45384 !SameBib(b1,b2)
weight w_1: Double;
factor: for Bib b1, Bib b2 add [sameBib(b1,b2)] * w_1;


// Author(b1,a1) ^ Author(b2,a2) => !SameAuthor(a1,a2)
//0.331704 !Author(b1,a1) v !Author(b2,a2) v !SameAuthor(a1,a2)
weight w_2: Double;
factor: for Author a1, Author a2 if author(_,a1) & author(_,a2) add [sameAuthor(a1,a2)] * w_2;




// Title(b1,t1) ^ Title(b2,t2) => !SameTitle(t1,t2)
//1.553 !Title(b1,t1) v !Title(b2,t2) v !SameTitle(t1,t2)
weight w_3: Double;
factor: for Title t1, Title t2 if title(_,t1) & title(_,t2) add [sameTitle(t1,t2)] * w_3;

// Venue(b1,v1) ^ Venue(b2,v2) => !SameVenue(v1,v2)
//1.69186 !Venue(b1,v1) v !Venue(b2,v2) v !SameVenue(v1,v2)
weight w_4: Double;
factor: for Venue v1, Venue v2 if venue(_,v1) & venue(_,v2) add [sameVenue(v1,v2)] * w_4;


//
// --- transitive closure
//

// SameBib(b1,b2) ^ SameBib(b2,b3) => SameBib(b1,b3)
//20.0 !SameBib(b1,b2) v !SameBib(b2,b3) v SameBib(b1,b3)
factor: for Bib b1, Bib b2, Bib b3 : sameBib(b1,b2) & sameBib(b2,b3) => sameBib(b1,b3);

//
// --- rules connecting evidence predicates to field match predicates
//

// - rules for similarity score for author

weight w_authorScore100: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore100(a1,a2)
  add [sameAuthor(a1,a2)] * w_authorScore100;


weight w_authorScore80: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore80(a1,a2)
  add [sameAuthor(a1,a2)] * w_authorScore80;

weight w_authorScore60: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore60(a1,a2)
  add [sameAuthor(a1,a2) * w_authorScore60;

weight w_authorScore40: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore40(a1,a2)
  add [sameAuthor(a1,a2)] * w_authorScore40;

weight w_authorScore20: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore20(a1,a2)
  add [sameAuthor(a1,a2)] * w_authorScore20;

weight w_authorScore0: Double;
factor:
  for Author a1, Author a2 if author(_,a1) & author(_,a2) & authorScore0(a1,a2)
  add [sameAuthor(a1,a2)] * w_authorScore0;


// - rules for similarity score for title
weight w_titleScore100: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore100(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore100;


weight w_titleScore80: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore80(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore80;

weight w_titleScore60: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore60(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore60;

weight w_titleScore40: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore40(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore40;

weight w_titleScore20: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore20(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore20;

weight w_titleScore0: Double;
factor:
  for Title a1, Title a2 if title(_,a1) & title(_,a2) & titleScore0(a1,a2)
  add [sameTitle(a1,a2)] * w_titleScore0;

// - rules for similarity score for venue

weight w_venueScore100: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore100(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore100;


weight w_venueScore80: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore80(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore80;

weight w_venueScore60: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore60(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore60;

weight w_venueScore40: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore40(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore40;

weight w_venueScore20: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore20(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore20;

weight w_venueScore0: Double;
factor:
  for Venue a1, Venue a2 if venue(_,a1) & venue(_,a2) & venueScore0(a1,a2)
  add [sameVenue(a1,a2)] * w_venueScore0;


//
// --- rules connecting bib-match predicates to field-match predicates
//

// - if the attributes are same, then corresponding bib entries are also same.

// Author(b1,a1) ^ Author(b2,a2) ^ SameAuthor(a1,a2) => SameBib(b1,b2)
//1.58212 !Author(b1,a1) v !Author(b2,a2) v !SameAuthor(a1,a2) v SameBib(b1,b2)
weight w_authorbib : Double;
factor:
  for Bib b1, Bib b2, Author a1, Author a2 if author(b1,a1) & author(b2,a2)
  add [sameAuthor(a1,a2) => sameBib(b1,b2)] * w_authorbib;


// Title(b1,t1) ^ Title(b2,t2) ^ SameTitle(t1,t2) => SameBib(b1,b2)
//4.87382 !Title(b1,t1) v !Title(b2,t2) v !SameTitle(t1,t2) v SameBib(b1,b2)
weight w_titlebib : Double;
factor:
  for Bib b1, Bib b2, Title a1, Title a2 if title(b1,a1) & title(b2,a2)
  add [sameTitle(a1,a2) => sameBib(b1,b2)] * w_titlebib;

// Venue(b1,v1) ^ Venue(b2,v2) ^ SameVenue(v1,v2) => SameBib(b1,b2)
//5.6291 !Venue(b1,v1) v !Venue(b2,v2) v !SameVenue(v1,v2) v SameBib(b1,b2)
weight w_venuebib : Double;
factor:
  for Bib b1, Bib b2, Venue a1, Venue a2 if venue(b1,a1) & venue(b2,a2)
  add [sameVenue(a1,a2) => sameBib(b1,b2)] * w_venuebib;


// - if the attributes are same, then corresponding bib entries are also same.

// Author(b1,a1) ^ Author(b2,a2) ^ SameBib(b1,b2) => SameAuthor(a1,a2)
//6.599 !Author(b1,a1) v !Author(b2,a2) v !SameBib(b1,b2) v SameAuthor(a1,a2)
weight w_bibauthor : Double;
factor:
  for Bib b1, Bib b2, Author a1, Author a2 if author(b1,a1) & author(b2,a2)
  add [sameBib(a1,a2) => sameAuthor(b1,b2)] * w_bibauthor;

// Title(b1,t1) ^ Title(b2,t2) ^ SameBib(b1,b2) => SameTitle(t1,t2)
//8.09818 !Title(b1,t1) v !Title(b2,t2) v !SameBib(b1,b2) v SameTitle(t1,t2)
weight w_bibtitle : Double;
factor:
  for Bib b1, Bib b2, Title a1, Title a2 if title(b1,a1) & title(b2,a2)
  add [sameBib(a1,a2) => sameTitle(b1,b2)] * w_bibtitle;

// Venue(b1,v1) ^ Venue(b2,v2) ^ SameBib(b1,b2) => SameVenue(v1,v2)
//3.50065 !Venue(b1,v1) v !Venue(b2,v2) v !SameBib(b1,b2) v SameVenue(v1,v2)
weight w_bibvenue : Double;
factor:
  for Bib b1, Bib b2, Venue a1, Venue a2 if venue(b1,a1) & venue(b2,a2)
  add [sameBib(a1,a2) => sameVenue(b1,b2)] * w_bibvenue;


//
// --- rules connecting the bib-match predicates directly to the evidence predicates
//

// - rules for similarity score for author

// Author(b1,a1) ^ Author(b2,a2) ^ AuthorScore100(a1,a2) => SameBib(b1,b2)
//0.460693 !Author(b1,a1) v !Author(b2,a2) v !AuthorScore100(a1,a2) v SameBib(b1,b2)
weight w_bibauthorScore100: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore100(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore100;

weight w_bibauthorScore80: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore80(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore80;

weight w_bibauthorScore60: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore60(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore60;

weight w_bibauthorScore40: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore40(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore40;

weight w_bibauthorScore20: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore20(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore20;

weight w_bibauthorScore0: Double;
factor:
  for Author a1, Author a2, Bib b1, Bib b2 if author(b1,a1) & author(b2,a2) & authorScore0(a1,a2)
  add [sameBib(b1,b2)] * w_bibauthorScore0;

weight w_bibtitleScore100: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore100(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore100;

weight w_bibtitleScore80: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore80(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore80;

weight w_bibtitleScore60: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore60(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore60;

weight w_bibtitleScore40: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore40(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore40;

weight w_bibtitleScore20: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore20(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore20;

weight w_bibtitleScore0: Double;
factor:
  for Title a1, Title a2, Bib b1, Bib b2 if title(b1,a1) & title(b2,a2) & titleScore0(a1,a2)
  add [sameBib(b1,b2)] * w_bibtitleScore0;

weight w_bibvenueScore100: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore100(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore100;

weight w_bibvenueScore80: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore80(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore80;

weight w_bibvenueScore60: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore60(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore60;

weight w_bibvenueScore40: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore40(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore40;

weight w_bibvenueScore20: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore20(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore20;

weight w_bibvenueScore0: Double;
factor:
  for Venue a1, Venue a2, Bib b1, Bib b2 if venue(b1,a1) & venue(b2,a2) & venueScore0(a1,a2)
  add [sameBib(b1,b2)] * w_bibvenueScore0;

