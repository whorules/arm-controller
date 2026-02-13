% !Mode:: "TeX:STY:UTF-8"
% IITPINFO DOCUMENT CLASS -- version alpha 1.27
% for Information Processes electronic Journal
% designed by V.Kozyakin and V.Venets
% on 19.01.2001
%
%CREDITS:
%
%As the basis for this class the LNCSE DOCUMENT CLASS -- version
%1.0 for Lecture Notes in Computational Science and Engineering by
%Springer Verlag (lsnce.cls) was taken in which plenty of changes
%and customizations were made. In particular:
%    a. some options were removed and instead option
%    "yap" was added
%    b. completely changed contents, headings, author list in
%    contents etc.
%    c. sizes, fonts, page layout were changed
%    d. plenty of commands to tune volume numbers, years of issue
%    etc. were made
%
%To make possible independent citations and equations referencing
%in different articles in the class were included parts from the
%packages chapterbib.sty Version 1.6 (27-SEP-1996) by Donald
%Arseneau and
%%     C I T E . S T Y
%%
%%     version 3.7  (April 1997)
%%
%%     Compressed, sorted lists of numerical citations: [11-16]
%%     see also OVERCITE.STY and DRFTCITE.STY
%%
%%     Copyright (C) 1989-1997 by Donald Arseneau
%%     These macros may be freely transmitted, reproduced, or modified
%%     provided that this notice is left intact.
%%
%The necessary customizations were made to remove unneeded options
%and to make possible independent referencing without using \input
%command as in chapterbib.sty.
%
%To allow forward/backward search it was added modified and
%shortened version of the srcltx.sty by Alexander Simonic
%
%
%The necessary strings from the packages indentfirst.sty and textcase.sty by David
%Carlisle were inserted too.
%
%======================================================================
\NeedsTeXFormat{LaTeX2e}[1995/12/01]
\ProvidesClass{iitpinfo_utf8}[2000/11/21 v1.0 ^^JLaTeX document class
for Information Processes]
% Options

% To avoid conflicts with calc.sty and mathtools.sty the command
% \setcounter is replaced by the equivalent \putcounter
% of its original definition
% V. Kozyakin

\def\putcounter#1#2{\@ifundefined{c@#1}{\@nocounterr{#1}}%
    {\global\csname c@#1\endcsname#2\relax}}
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\let\if@envcntreset\iffalse
\DeclareOption{envcountreset}{\let\if@envcntreset\iftrue}
\let\if@envcntsect\iffalse
\DeclareOption{envcountsect}{\let\if@envcntsect\iftrue}

\let\if@yap\iffalse
\DeclareOption{yap}{\let\if@yap\iftrue}
\let\if@ams\iffalse
\DeclareOption{ams}{\let\if@ams\iftrue}
\let\if@english\iffalse
\DeclareOption{english}{\let\if@english\iftrue}

\DeclareOption*{\PassOptionsToClass{\CurrentOption}{article}}

\ProcessOptions

 \LoadClass[a4paper,twoside,11pt]{article}
 \RequirePackage[T2A]{fontenc}
 \RequirePackage[utf8]{inputenc}
 \RequirePackage{textcase}

 \if@ams
 \RequirePackage{amsmath}
 \RequirePackage{amssymb}
 \RequirePackage{amsfonts}\fi
 \RequirePackage[english,russian]{babel}

\AtBeginDocument{\if@english\selectlanguage{english}\else\selectlanguage{russian}\fi}

 \if@yap \RequirePackage{graphicx}
\DeclareGraphicsRule{.gif}{bmp}{}{}
\DeclareGraphicsRule{.pcx}{bmp}{}{}
\DeclareGraphicsRule{.tga}{bmp}{}{}
 \ifx\pdfpageheight\@undefined
 \DeclareGraphicsRule{.png}{bmp}{}{}
 \DeclareGraphicsRule{.tif}{bmp}{}{}
 \DeclareGraphicsRule{.tiff}{bmp}{}{}
 \fi
\fi



\def\englishhyphenmins{\tw@\thr@@}%
%%
%% This is file `indentfirst.sty',
%% generated with the docstrip utility.
%%
%% The original source files were:
%%
%% indentfirst.dtx  (with options: `package')
%%
%% This is a generated file.
%%
%% Copyright 1993 1994 1995 1996 1997 1998 1999
%% The LaTeX3 Project and any individual authors listed elsewhere
%% in this file.
%%
%% This file is part of the Standard LaTeX `Tools Bundle'.
%% -------------------------------------------------------
%%
%% It may be distributed and/or modified under the
%% conditions of the LaTeX Project Public License, either version 1.1
%% of this license or (at your option) any later version.
%% The latest version of this license is in
%%    http://www.latex-project.org/lppl.txt
%% and version 1.1 or later is part of all distributions of LaTeX
%% version 1999/06/01 or later.
%%
%% The list of all files belonging to the LaTeX `Tools Bundle' is
%% given in the file `manifest.txt'.
%%
%% File: indent.dtx Copyright (C) 1991-1994 David Carlisle
\let\@afterindentfalse\@afterindenttrue
\@afterindenttrue
%%
%% End of file `indentfirst.sty'.


\AtEndDocument{\label{Lastp@ge}\clearpage}

\def\@seccntformat#1{\csname the#1\endcsname.~}


\setlength{\textwidth}{16.5cm} \setlength{\textheight}{23.5cm}% !!!
\setlength{\oddsidemargin}{-0.29cm}% !!!
\setlength{\evensidemargin}{-0.29cm}% !!!
\setlength{\marginparwidth}{25pt}% !!!
\setlength{\topmargin}{-1.29cm}% !!! 1.11=2.54-1.25
\setlength\headsep{-0.54cm}% !!!
\addtolength{\headsep}{-\topmargin}% !!!
\addtolength{\headsep}{-\headheight}% !!!
\newlength{\ArticleHeadDepth}
\setlength{\ArticleHeadDepth}{1.25cm}
\newlength{\FirstHeadDepth}
\setlength{\FirstHeadDepth}{\ArticleHeadDepth}
\newlength{\dblrulegap}
\setlength{\dblrulegap}{2.7pt}
\newcommand{\dblrulefill}[1]{\vbox{\hrule height0.3pt width#1\vskip%
\dblrulegap\hrule height0.3pt width#1}}
\newlength{\dblruleheight}
\settoheight{\dblruleheight}{\dblrulefill{\textwidth}}
\newlength{\dblrulegapC}
\setlength{\dblrulegapC}{2pt}
\newcommand\dblrulefillC{\vbox{\hrule height1pt width\textwidth\vskip%
\dblrulegap\hrule height1pt width\textwidth}}
\newlength{\dblruleheightC}
\settoheight{\dblruleheightC}{\dblrulefillC}
\newlength{\@fhd}
\newlength{\@rubwidth}
\newlength{\@restrub}
\settoheight{\footskip}{\footnote A}%
\addtolength{\footskip}{0.5cm}%

\widowpenalty=10000%
\clubpenalty=10000%

% LaTeX does not provide a command to enter the authors institute
% addresses. The \institute command is defined here.

\newcounter{l@stp@ge}
\newcounter{@prevpage}
\newcounter{@inst}
\newcounter{@auth}
\newcounter{auco}
\def\andname{and}
\def\lastandname{\unskip, and}
\newdimen\instindent
\newbox\authrun
\newbox\titrun

\def\title#1{\gdef\@title{#1}}

\def\received#1{\gdef\@received{#1}}%
\edef\@received{\noexpand\selectlanguage{russian}\textbackslash received\{...\} --- ???}%Received~
\def\titlerunning#1{\gdef\@titlerunning{#1}}\edef\@titlerunning{}%
\def\authorrunning#1{\gdef\@authorrunning{#1}}
\edef\@authorrunning{}%

\def\JournalName#1{\gdef\@JournalName{#1}}%
\edef\@JournalName{\noexpand\cyr%
\CYRI\cyrn\cyrf\cyro\cyrr\cyrm\cyra\cyrc\cyri\cyro\cyrn\cyrn\cyrery\cyre%
\ \cyrp\cyrr\cyro\cyrc\cyre\cyrs\cyrs\cyrery}%
\def\JournalISSNCode#1{\gdef\@JournalISSNCode{#1}}\edef\@JournalISSNCode{0005-1179}%CODE-CODE
\def\YearOfIssue#1{\gdef\@YearOfIssue{#1}}\edef\@YearOfIssue{111?}%
\def\CopyrightYear#1{\gdef\@CopyrightYear{#1}}%
\gdef\@CopyrightYear{\textbackslash CopyrightYear\{...\} --- ???}%
\def\VolumeNo#1{\gdef\@VolumeNo{#1}}\edef\@VolumeNo{4?}%
\def\IssueNo#1{\gdef\@IssueNo{#1}}\edef\@IssueNo{5?}%
\def\CopyrightedAuthors#1{\gdef\@CopyrightedAuthors{\selectlanguage{russian}{#1}}}%
\gdef\@CopyrightedAuthors{\textbackslash CopyrightedAuthors\{...\} --- ???}%
\def\ArticleDataHeadA#1{\gdef\@ArticleDataHeadA{#1}}%
\edef\@ArticleDataHeadA{\noexpand\rmfamily\noexpand\@JournalName,\noexpand\cyr%
~\CYRT\cyro\cyrm~\noexpand\@VolumeNo,~\textnumero~\noexpand\@IssueNo,~%
\noexpand\@YearOfIssue,%
~\cyrs\cyrt\cyrr.~\noexpand\thepage--\noexpand\l@stp@geref.}%
\edef\@ArticleDataHeadA@{\noexpand\rmfamily\noexpand\@JournalName,\noexpand\cyr%
~\CYRT\cyro\cyrm~\noexpand\@VolumeNo,~\textnumero~\noexpand\@IssueNo,~%
\noexpand\@YearOfIssue,%
~\cyrs\cyrt\cyrr.~\noexpand\thepage.}%
\def\ArticleDataHeadC#1{\gdef\@ArticleDataHeadC{#1}}\edef\@ArticleDataHeadC%
{\noexpand\rmfamily\noexpand\selectlanguage{russian}\noexpand\copyright~\noexpand\@CopyrightYear~%
~\noexpand\@CopyrightedAuthors.}%
\def\ArticleDataFoot#1{\gdef\@ArticleDataFoot{#1}}%

\def\Rubric#1{\clearpage%
\gdef\@Rubric{\raisebox{.5\dblruleheight}%
{\raisebox{-.5\height}{\normalfont\large\bfseries\shortstack{\noexpand\selectlanguage{russian}\MakeTextUppercase{#1}}}}}}%
\gdef\@Rubric{\normalfont\large\bfseries\textbackslash Rubric\{...\} --- ???}%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\def\f@urdigit#1{%
\begingroup\count0=#1%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\one@mydate{\the\count1}%
\divide\count0by10%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\two@mydate{\the\count1}%
\divide\count0by10%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\three@mydate{\the\count1}%
\divide\count0by10%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\four@mydate{\the\count1}%
\endgroup%
\four@mydate\three@mydate\two@mydate\one@mydate}

\def\tw@digit#1{%
\begingroup\count0=#1%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\one@mydate{\the\count1}%
\divide\count0by10%
\count1=\count0\divide\count1by10\multiply\count1by-10\advance\count1by\count0%
\xdef\two@mydate{\the\count1}%
\endgroup%
\two@mydate\one@mydate}


\edef\@ArticleDataFoot{}

\def\clearheadinfo{%
\gdef\@author{\textbackslash author\{...\} --- ???}%
\gdef\@title{\textbackslash title\{...\} --- ???}%
\gdef\@subtitle{}%
\gdef\@institute{\textbackslash institute\{...\} --- ???}%
\gdef\@thanks{}%
\gdef\@deceased{}%
\gdef\@titlerunning{}%
\gdef\@authorrunning{}}%


\setlength\footnotesep{7.7\p@}
\setlength\textfloatsep{6mm\@plus 2\p@ \@minus 4\p@} %8
\setlength\intextsep   {6mm\@plus 2\p@ \@minus 2\p@}%8



  \def\bibcite#1#2{\global\@namedef{b@#1}{#2}}

 \def\@citex[#1]#2{%
   \let\@citea\@empty
   \@cite{\@for\@citeb:=#2\do
     {\@citea\let\@citea\citepunct
      \edef\@citeb{\expandafter\@firstofone\@citeb}%
      \if@filesw\immediate\write\@auxout{\string\citation{\@citeb}}\fi
      \@ifundefined{b@\@citeb}{\mbox{\reset@font\bf ?}%
         \@warning{Citation `\@citeb' in file `\@currentipfile'
             on page \thepage \space undefined}\G@refundefinedtrue}%
      {\hbox{\citeform{\csname b@\@citeb\endcsname}}}}}{#1}}
%
\def\nocite#1{\@bsphack %
  \@for\@citeb:=#1\do{%
    \edef\@citeb{\expandafter\@firstofone\@citeb}%
    \if@filesw\immediate\write\@auxout{\string\citation{\@citeb}}\fi
    \@ifundefined{b@\@citeb}{\G@refundefinedtrue
        \@warning{Citation `\@citeb' in file `\@currentipfile' undefined}}{}}%
  \@esphack}
%

\def\@firstofone#1{#1}

\@ifundefined{citeform}{\let\citeform=\relax}{}
\@ifundefined{citepunct}{\def\citepunct{,\penalty\@m\ }}{}

\xdef\@savedjobname{\jobname}% save root file name as a macro
\let\@currentipfile\@savedjobname

\@ifundefined{reset@font}{\let\reset@font\relax}{}
\@ifundefined{G@refundefinedtrue}{\let\G@refundefinedtrue\relax}{}

\let\cb@bibliographystyle\bibliographystyle
\def\cb@roottest{2\ifx\the@ipfilectr\@empty 1\else2\fi}

\def\bibliographystyle#1{\if\cb@roottest\cb@bibliographystyle{#1}\fi}

\def\bibliography#1{\if\cb@roottest
   \if@filesw\immediate\write\@auxout{\string\bibdata{#1}}\fi
   \fi \@input{\@currentipfile.bbl}}



\def\sectionbib#1#2{% Macro to convert \thebibliography command
 \long\def\@tempa##1##{% to parse beginning of old def
  \long\def\@tempa####1\endthebibliography{}\def\@tempb{##1}\@tempa}%
 \expandafter\@tempa\thebibliography{}{}\endthebibliography
 \begingroup \toks@{#1}%
 \cb@warning {The sectionbib option changes \string"\expandafter
  \strip@prefix\meaning\@tempb\string" to \string"\the\toks@\string"
  in \string\thebibliography }\endgroup
 \let\cb@thebibliography\thebibliography
 \def\thebibliography{\let\@currentlabel\@empty
   \ifx\the@ipfilectr\@empty \else
   \expandafter\expandafter\expandafter\cb@sectionbib\fi
   \cb@thebibliography}%
 \expandafter\def\expandafter\cb@sectionbib\@tempb##1{#1{\bibname
 \ifx\@mkboth\@gobbletwo\else\markright{\MakeTextUppercase{\bibname}}\fi% like \@mkright
 \ifx\@currentlabel\@empty \addcontentsline{toc}{#2}{\bibname}\fi}}}

\def\strip@prefix#1>{}
\@ifundefined{bibname}{\def\bibname{Bibliography}}{}


\long\def\my@second#1#2#3#4#5{#2}%
\def\my@setref#1#2#3{%
  \ifx#1\relax
   \expandafter\@setref\csname r@lastp@ge\endcsname\my@second{lastp@ge}%
   \else
   \expandafter#2#1\null%
  \fi}
\def\l@stp@geref{%
\expandafter\my@setref\csname%
r@Lastp@ge\endcsname\my@second{Lastp@ge}%
}


\putcounter{secnumdepth}{2}

\newcounter {chapter}
\renewcommand\thechapter      {\@arabic\c@chapter}

\newif\if@mainmatter \@mainmattertrue
\newcommand\frontmatter{\cleardoublepage
            \@mainmatterfalse\pagenumbering{Roman}}
\newcommand\mainmatter{\cleardoublepage
       \@mainmattertrue\pagenumbering{arabic}}
\newcommand\backmatter{\if@openright\cleardoublepage\else\clearpage\fi
      \@mainmatterfalse}

\renewcommand\part{\cleardoublepage
                 \thispagestyle{empty}%
                 \if@twocolumn
                     \onecolumn
                     \@tempswatrue
                   \else
                     \@tempswafalse
                 \fi
                 \null\vfil
                 \secdef\@part\@spart}

\def\@part[#1]#2{%
    \ifnum \c@secnumdepth >-2\relax
      \refstepcounter{part}%
      \addcontentsline{toc}{part}{\thepart\hspace{1em}#1}%
    \else
      \addcontentsline{toc}{part}{#1}%
    \fi
    \markboth{}{}%
    {\centering
     \interlinepenalty \@M
     \normalfont
     \ifnum \c@secnumdepth >-2\relax
       \huge\bfseries \partname~\thepart
       \par
       \vskip 20\p@
     \fi
     \Huge \bfseries #2\par}%
    \@endpart}
\def\@spart#1{%
    {\centering
     \interlinepenalty \@M
     \normalfont
     \Huge \bfseries #1\par}%
    \@endpart}
\def\@endpart{\vfil\newpage
              \if@twoside
                \null
                \thispagestyle{empty}%
                \newpage
              \fi
              \if@tempswa
                \twocolumn
              \fi}

\newcommand\chapter{\clearpage
                    \thispagestyle{empty}%
                    \global\@topnum\z@
                    \@afterindentfalse
                    \secdef\@chapter\@schapter}
\def\@chapter[#1]#2{\ifnum \c@secnumdepth >\m@ne
                       \if@mainmatter
                         \refstepcounter{chapter}%
                         \typeout{\@chapapp\space\thechapter.}%
                         \addcontentsline{toc}{chapter}%
                                  {\protect\numberline{\thechapter}#1}%
                       \else
                         \addcontentsline{toc}{chapter}{#1}%
                       \fi
                    \else
                      \addcontentsline{toc}{chapter}{#1}%
                    \fi
                    \chaptermark{#1}%
                    \addtocontents{lof}{\protect\addvspace{10\p@}}%
                    \addtocontents{lot}{\protect\addvspace{10\p@}}%
                    \if@twocolumn
                      \@topnewpage[\@makechapterhead{#2}]%
                    \else
                      \@makechapterhead{#2}%
                      \@afterheading
                    \fi}
\def\@makechapterhead#1{%
% \vspace*{50\p@}%
  {\parindent \z@ \center
    \normalfont
    \ifnum \c@secnumdepth >\m@ne
      \if@mainmatter
        \large\bfseries \@chapapp{} \thechapter
        \par\nobreak
        \vskip 20\p@
      \fi
    \fi
    \interlinepenalty\@M
    \Large \bfseries #1\par\nobreak
    \vskip 40\p@
  }}
\def\@schapter#1{\if@twocolumn
                   \@topnewpage[\@makeschapterhead{#1}]%
                 \else
                   \@makeschapterhead{#1}%
                   \@afterheading
                 \fi}
\def\@makeschapterhead#1{%
% \vspace*{50\p@}%
  {\parindent \z@ \center
    \normalfont
    \interlinepenalty\@M
    \Large \bfseries  #1\par\nobreak
    \vskip 40\p@
  }}

\renewcommand\appendix{\par\putcounter{section}{0}\putcounter{subsection}{0}
\putcounter{equation}{0} \vskip 18\p@ \@plus 4\p@ \@minus 4\p@
\begin{flushright}{\itshape\MakeTextUppercase{\appendixname}}\end{flushright}
\vskip 4\p@ \@plus 2\p@ \@minus 1\p@
\def\thesection{\@Alph\c@section}
\def\theequation{A.\arabic{equation}}}


\newcommand\acknowledgments{\medskip\centerline{\normalfont\normalsize%
                       \uppercase{Acknowledgments}%\bfseries\boldmath
                        %\rightskip=\z@ \@plus 8em\pretolerance=10000
                        }\par}

\renewcommand\section{\@startsection{section}{1}{\z@}%
                       {-18\p@ \@plus -4\p@ \@minus -4\p@}%
                       {4\p@ \@plus 2\p@ \@minus 1\p@}%
                       {\normalfont\normalsize\center\MakeTextUppercase%\bfseries\boldmath
                        %\rightskip=\z@ \@plus 8em\pretolerance=10000
                        }}

\renewcommand\subsection{\@startsection{subsection}{2}{\z@}%
                       {-18\p@ \@plus -4\p@ \@minus -4\p@}%
                       {4\p@ \@plus 2\p@ \@minus 1\p@}%
                       {\normalsize\itshape\center%\bfseries\boldmath
                        %\rightskip=\z@ \@plus 8em\pretolerance=10000
                        }}
\renewcommand\subsubsection{\@startsection{subsubsection}{3}{\leftmargini}%
                       {-18\p@ \@plus -4\p@ \@minus -4\p@}%
                       {4\p@ \@plus 2\p@ \@minus 1\p@}%
                       {\normalsize\bfseries\boldmath\center
                       }}
\renewcommand\paragraph{\@startsection{paragraph}{4}{\leftmargini}%
                       {-12\p@ \@plus -4\p@ \@minus -4\p@}%
                       {-0.5em \@plus -0.22em \@minus -0.1em}%
                       {\normalfont\normalsize\itshape}}
\renewcommand\subparagraph[1]{\typeout{Warning: You should not use
                  \string\subparagraph\space with this class}\vskip0.5cm
You should not use \verb|\subparagraph| with this class.\vskip0.5cm}


\setlength\parindent{5mm}%
\setlength\parskip{2pt}%
\setlength\leftmargini{5mm}%
\newlength{\captwidth}%
\setlength{\captwidth}{\textwidth}%
\addtolength{\captwidth}{-2\leftmargini}

\setlength\leftmargin{\leftmargini}%
\setlength\leftmarginii{\leftmargini}%
\setlength\leftmarginiii{\leftmargini}%
\setlength\leftmarginiv{\leftmargini}%
\setlength\labelsep{.5em}%
\setlength\labelwidth{\leftmargini}%
\addtolength\labelwidth{-\labelsep}%

\def\@listI{\leftmargin\leftmargini
            \parsep 0\p@ \@plus1\p@ \@minus\p@
            \topsep 8\p@ \@plus2\p@ \@minus4\p@
            \itemsep0\p@}
\let\@listi\@listI
\@listi
\def\@listii {\leftmargin\leftmarginii
              \labelwidth\leftmarginii
              \advance\labelwidth-\labelsep
              \topsep    0\p@ \@plus2\p@ \@minus\p@}
\def\@listiii{\leftmargin\leftmarginiii
              \labelwidth\leftmarginiii
              \advance\labelwidth-\labelsep
              \topsep    0\p@ \@plus\p@\@minus\p@
              \parsep    \z@
              \partopsep \p@ \@plus\z@ \@minus\p@}

\renewcommand\labelitemi{\normalfont\bfseries --}
\renewcommand\labelitemii{$\m@th\bullet$}

\setlength\arraycolsep{1.4\p@}
\setlength\tabcolsep{4\p@}%1.4




\def\l@part#1#2{\addpenalty{\@secpenalty}%
   \addvspace{2em plus\p@}%  % space above part line
   \begingroup
     \parindent \z@
     \rightskip \z@ plus 5em
     \hrule\vskip5pt
     \bfseries\boldmath   % set line in boldface
     \leavevmode          % TeX command to enter horizontal mode.
     #1\par
     \vskip5pt
     \hrule
     \vskip1pt
     \nobreak             % Never break after part entry
   \endgroup}

\def\@dotsep{2}

\def\addnumcontentsmark#1#2#3{%
\addtocontents{#1}{\protect\contentsline{#2}{\protect\numberline
                                    {\thechapter}#3}{\thepage}}}
\def\addcontentsmark#1#2#3{%
\addtocontents{#1}{\protect\contentsline{#2}{#3}{\thepage}}}
\def\addcontentsmarkwop#1#2#3{%
\addtocontents{#1}{\protect\contentsline{#2}{#3}{0}}}

\def\@adcmk[#1]{\ifcase #1 \or
\def\@gtempa{\addnumcontentsmark}%
  \or    \def\@gtempa{\addcontentsmark}%
  \or    \def\@gtempa{\addcontentsmarkwop}%
  \fi\@gtempa{toc}{chapter}}
\def\addtocmark{\@ifnextchar[{\@adcmk}{\@adcmk[3]}}

\def\l@chapter#1#2{\addpenalty{-\@highpenalty}
 \vskip 1.0em plus 1pt \@tempdima 1.5em \begingroup
 \parindent \z@ \rightskip \@pnumwidth
 \parfillskip -\@pnumwidth
 \leavevmode \advance\leftskip\@tempdima \hskip -\leftskip
 {\large\bfseries\boldmath#1}\ifx0#2\hfil\null
 \else
      \nobreak
      \leaders\hbox{$\m@th \mkern \@dotsep mu.\mkern
      \@dotsep mu$}\hfill
      \nobreak\hbox to\@pnumwidth{\hss #2}%
 \fi\par
 \penalty\@highpenalty \endgroup}

\def\l@title#1#2{\addpenalty{-\@highpenalty}
 \addvspace{8pt plus 1pt}
 \@tempdima \z@
 \begingroup
 \parindent \z@ \rightskip \@tocrmarg
 \parfillskip -\@tocrmarg
 \leavevmode \advance\leftskip\@tempdima \hskip -\leftskip
 #1\nobreak\hfill
 \nobreak\hbox to\@pnumwidth{\hss #2}\par
 \penalty\@highpenalty \endgroup}


\putcounter{tocdepth}{0}
\newdimen\tocchpnum
\newdimen\tocsecnum
\newdimen\tocsectotal
\newdimen\tocsubsecnum
\newdimen\tocsubsectotal
\newdimen\tocsubsubsecnum
\newdimen\tocsubsubsectotal
\newdimen\tocparanum
\newdimen\tocparatotal
\newdimen\tocsubparanum
\tocchpnum=\z@            % no chapter numbers
\tocsecnum=15\p@          % section 88. plus 2.222pt
\tocsubsecnum=23\p@       % subsection 88.8 plus 2.222pt
\tocsubsubsecnum=27\p@    % subsubsection 88.8.8 plus 1.444pt
\tocparanum=35\p@         % paragraph 88.8.8.8 plus 1.666pt
\tocsubparanum=43\p@      % subparagraph 88.8.8.8.8 plus 1.888pt
\def\calctocindent{%
\tocsectotal=\tocchpnum
\advance\tocsectotal by\tocsecnum
\tocsubsectotal=\tocsectotal
\advance\tocsubsectotal by\tocsubsecnum
\tocsubsubsectotal=\tocsubsectotal
\advance\tocsubsubsectotal by\tocsubsubsecnum
\tocparatotal=\tocsubsubsectotal
\advance\tocparatotal by\tocparanum}
\calctocindent

\def\l@section{\@dottedtocline{1}{\tocchpnum}{\tocsecnum}}
\def\l@subsection{\@dottedtocline{2}{\tocsectotal}{\tocsubsecnum}}
\def\l@subsubsection{\@dottedtocline{3}{\tocsubsectotal}{\tocsubsubsecnum}}
\def\l@paragraph{\@dottedtocline{4}{\tocsubsubsectotal}{\tocparanum}}
\def\l@subparagraph{\@dottedtocline{5}{\tocparatotal}{\tocsubparanum}}

\def\listoffigures{\@restonecolfalse\if@twocolumn\@restonecoltrue\onecolumn
 \fi\section*{\listfigurename\@mkboth{{\listfigurename}}{{\listfigurename}}}
 \@starttoc{lof}\if@restonecol\twocolumn\fi}
\def\l@figure{\@dottedtocline{1}{0em}{1.5em}}

\def\listoftables{\@restonecolfalse\if@twocolumn\@restonecoltrue\onecolumn
 \fi\section*{\listtablename\@mkboth{{\listtablename}}{{\listtablename}}}
 \@starttoc{lot}\if@restonecol\twocolumn\fi}
\let\l@table\l@figure

\renewcommand\listoffigures{%
    \section*{\listfigurename
      \@mkboth{\listfigurename}{\listfigurename}}%
    \@starttoc{lof}%
    }

\renewcommand\listoftables{%
    \section*{\listtablename
      \@mkboth{\listtablename}{\listtablename}}%
    \@starttoc{lot}%
    }

\renewenvironment{thebibliography}[1]
     {\section*{\refname}
      \def\@biblabel##1{##1.}
      \small
      \list{\@biblabel{\@arabic\c@enumiv}}%
           {\settowidth\labelwidth{\@biblabel{#1}}%
            \leftmargin\labelwidth
            \advance\leftmargin\labelsep
            \usecounter{enumiv}%
            \let\p@enumiv\@empty
            \renewcommand\theenumiv{\@arabic\c@enumiv}}%
            \renewcommand\newblock{\hskip .11em \@plus.33em \@minus.07em}%
      \sloppy\clubpenalty4000\widowpenalty4000%
      \sfcode`\.=\@m}
     {\def\@noitemerr
       {\@latex@warning{Empty `thebibliography' environment}}%
      \endlist}

% First, ensure that some catcodes have the expected values
\edef\citenum{% to restore funny codes
  \catcode\string`\string ` \the\catcode\string`\`
  \catcode\string`\string ' \the\catcode\string`\'
  \catcode\string`\string = \the\catcode\string`\=
  \catcode\string`\string _ \the\catcode\string`\_
  \catcode\string`\string : \the\catcode\string`\:}
\catcode\string`\`12 \catcode`\'12 \catcode`\=12 \catcode`\_8
\catcode`\:12

%   Handle optional variations:  [verbose,nospace,space,nosort],
%   \citeform,\citeleft,\citeright,\citemid,\citepunct
%
%   Set defaults:

%   [ on the left.
\let\citeleft=[
%   ] on the right:
\let\citeright=]
%   , (comma space) before note
\def\citemid{,\penalty\@medpenalty\ }
%   , (comma thin-space) between entries; [nospace] eliminates the space
\def\citepunct{,\penalty\@m\hskip.13emplus.1emminus.1em}%
%   Each number left as-is:
\def\citeform{}
%   Do not repeat warnings.  [verbose] reverses
\let\oc@verbo\relax
\edef\citen{\noexpand\protect \expandafter\noexpand\csname citen
\endcsname}
\@namedef{citen }#1{%
\@nocite{#1}% ignores spaces, writes to .aux file, returns #1 in \@no@sparg
\@tempcntb\m@ne    % \@tempcntb tracks highest number
\let\@h@ld\relax   % nothing held from list yet
\let\@citea\@empty % no punctuation preceding first
\let\@celt\delimiter % an unexpandable, but identifiable, token
\def\@cite@list{}% % empty list to start
\@for \@citeb:=\@no@sparg\do{\@make@cite@list}% make a sorted list of numbers
% After sorted citelist is made, execute it to compress citation ranges.
\@tempcnta\m@ne    % no previous number
\let\@celt\@compress@cite \@cite@list % output number list with compression
\@h@ld}% output anything held over

% For each citation, check if it is defined and if it is a number.
% if a number: insert it in the sorted \@cite@list
% otherwise: output it immediately.
%
\def\@make@cite@list{%
 \expandafter\let \expandafter\@B@citeB
          \csname b@\@citeb\endcsname
 \ifx\@B@citeB\relax % undefined: output ? and warning
    \@citea {\bf{?}}\let\@citea\citepunct \G@refundefinedtrue
    \@warning {Citation `\@citeb' on page \thepage\space undefined}%
    \oc@verbo \global\@namedef{b@\@citeb}{?}%
 \else %  defined               % remove previous line to repeat warnings
    \ifcat _\ifnum\z@<0\@B@citeB _\else A\fi % a positive number, put in list
       \@addto@cite@list
    \else % citation is not a number, output immediately
       \@citea \citeform{\@B@citeB}%
       \let\@citea\citepunct
 \fi\fi}

% Regular definition for adding entry to cite list, with sorting

\@ifundefined{@addto@cite@list}{% sort option either declared or defaulted
  \def\@addto@cite@list{\@tempcnta\@B@citeB \relax
     \ifnum \@tempcnta>\@tempcntb % new highest, add to end (efficiently)
        \edef\@cite@list{\@cite@list \@celt{\@B@citeB}}%
        \@tempcntb\@tempcnta
     \else % arbitrary number: insert appropriately
        \edef\@cite@list{\expandafter\@sort@celt \@cite@list \@gobble @}%
     \fi}
%
% \@sort@celt inserts number (\@tempcnta) into list of \@celt{num} (#1{#2})
% \@celt must not be expandable; list should end with two vanishing tokens.
%
  \def\@sort@celt#1#2{\ifx \@celt #1% parameters are \@celt {num}
       \ifnum #2<\@tempcnta % number goes later in list
          \@celt{#2}%
          \expandafter\expandafter\expandafter\@sort@celt % continue
       \else % number goes here
          \@celt{\number\@tempcnta}\@celt{#2}% stop comparing
    \fi\fi}
}% end sort option definitions
{}% nosort definition is done in options processing

% Check if each number follows previous and can be put in a range
%
\def\@compress@cite#1{%  % This is executed for each number
  \advance\@tempcnta\@ne % Now \@tempcnta is one more than the previous number
  \ifnum #1=\@tempcnta   % Number follows previous--hold on to it
     \ifx\@h@ld\relax    % first pair of successives
        \edef\@h@ld{\@citea \noexpand\citeform{#1}}%
     \else               % compressible list of successives
        \def\@h@ld{\hbox{--}\penalty\@m \citeform{#1}}%
     \fi % (using \hbox avoids easy \exhyphenpenalty breaks)
  \else   %  non-successor -- dump what's held and do this one
     \@h@ld \@citea \citeform{#1}\let\@h@ld\relax
  \fi \@tempcnta#1\let\@citea\citepunct
}

\edef\cite{\noexpand\protect\expandafter\noexpand\csname cite
\endcsname}

\@namedef{cite }{\@ifnextchar
[{\@tempswatrue\@citex}{\@tempswafalse\@citex[]}}

%  Make \@citex refer to \citen:
%
\def\@citex[#1]#2{\@cite{\citen{#2}}{#1}}%

%  Replacement for \@cite which defines the formatting normally done
%  around the citation list.  Put a penalty before the citation.  Also,
%  adjust the spacing: if no space or if there is extra space due to some
%  punctuation, then change to one inter-word space.  Minor changes can
%  be done by changing \citeleft, \citemid, and \citeright; but in other
%  cases it might be necessary to redefine the whole macro.
%
\def\@cite#1#2{\leavevmode
  \@tempskipa\lastskip \edef\@tempa{\the\@tempskipa}\unskip
  \ifnum\lastpenalty=\z@ \penalty\@highpenalty \fi
  \ifx\@tempa\@zero@skip \spacefactor1000 \fi % if no space before, set flag
  \ifnum\spacefactor>\@m \ \else \hskip\@tempskipa \fi
  \citeleft{#1\if@tempswa \citemid #2\fi
  \spacefactor\@m % punctuation in note doesn't affect outside
  }\citeright}

\edef\@zero@skip{\the\z@skip}

% \nocite: This is changed to ignore *ALL* spaces and be robust.  The
% parameter list, with spaces removed, is `returned' in \@no@sparg, which
% is used by \citen.
%
\edef\nocite{\noexpand\protect\expandafter\noexpand\csname nocite
\endcsname}

\@namedef{nocite }#1{\@bsphack \@nocite{#1}%
 \@for \@citeb:=\@no@sparg\do{\@ifundefined{b@\@citeb}%
    {\G@refundefinedtrue\@warning{Citation `\@citeb' undefined}%
    \oc@verbo \global\@namedef{b@\@citeb}{?}}{}}%
 \@esphack}

\def\@nocite#1{\xdef\@no@sparg{\@ignsp#1 \! }% remove *ALL* spaces
 \if@filesw \immediate\write\@auxout {\string\citation {\@no@sparg}}\fi}

% for ignoring *ALL* spaces in the input.  This presumes there are no
% \outer tokens and no \if-\fi constructs in the parameter.  Spaces inside
% braces are retained.
%
\def\@ignsp#1 {\ifx\!#1\@empty\else #1\expandafter\@ignsp\fi}

\let\nocitecount\relax  % in case \nocitecount was used for drftcite


\@ifundefined{G@refundefinedtrue}{\let\G@refundefinedtrue\relax}{}

\citenum % execute restore-catcodes

% Aliases:
\let\citenum\citen
\let\citeonline\citen



\def\idxquad{\hskip 10\p@}% space that divides entry from number

\def\@idxitem{\par\hangindent 10\p@}

\def\subitem{\par\setbox0=\hbox{--\enspace}% second order
                \noindent\hangindent\wd0\box0}% index entry

\def\subsubitem{\par\setbox0=\hbox{--\,--\enspace}% third
                \noindent\hangindent\wd0\box0}% order index entry

\def\indexspace{\par \vskip 10\p@ plus5\p@ minus3\p@\relax}

\let\if@threecolind\iffalse
\def\threecolindex{\let\if@threecolind\iftrue}
\renewenvironment{theindex}
               {\if@twocolumn
                  \@restonecolfalse
                \else
                  \@restonecoltrue
                \fi
                \columnseprule \z@
                \columnsep 1cc
                \@nobreaktrue
                \if@threecolind
                   \begin{multicols}{3}[\chapter*{\indexname}]%
                \else
                   \twocolumn[\@makeschapterhead{\indexname}]%
                \fi
                \@mkboth{\indexname}{\indexname}%
                \thispagestyle{empty}\parindent\z@
                \rightskip0\p@ plus 40\p@
                \parskip\z@ \@plus .3\p@\relax
                \let\item\@idxitem
                \def\,{\relax\ifmmode\mskip\thinmuskip
                             \else\hskip0.2em\ignorespaces\fi}%
                \normalfont\small}
               {\if@threecolind\end{multicols}\fi
                \global\let\if@threecolind\iffalse
                \if@restonecol\onecolumn\else\clearpage\fi}

\renewcommand\footnoterule{%
  \kern-3\p@
  \hrule\@width 3truecm
  \kern2.5\p@}
\long\def\@makefntext#1{%
    \parindent 1em%
    \leftskip 1em%
    \noindent
    \llap{\hb@xt@1em{\hss\@makefnmark\ }}\ignorespaces#1}

\long\def\@makecaption#1#2{%
  \vskip\abovecaptionskip%
  \setlength{\leftskip}{\leftmargini}%
  \setlength{\rightskip}{\leftmargini}%
  \sbox\@tempboxa{\footnotesize{\bfseries #1.} #2}%
  \ifdim \wd\@tempboxa >\hsize
    \footnotesize{\bfseries #1.} #2\par
  \else
    \global \@minipagefalse
    \hb@xt@\hsize{\hfil\box\@tempboxa\hfil}%
  \fi
  \vskip\belowcaptionskip}

\def\fps@figure{htbp}
\def\fnum@figure{\figurename\thinspace\thefigure}
\def \@floatboxreset {%
        \reset@font
        \footnotesize
        \@setnobreak
        \@setminipage
}
\def\fps@table{htbp}
\def\fnum@table{\tablename~\thetable}
\renewenvironment{table}
               {\setlength\abovecaptionskip{2\p@}%
                \setlength\belowcaptionskip{2\p@}%
                \@float{table}}
               {\end@float}
\renewenvironment{table*}
               {\setlength\abovecaptionskip{2\p@}%
                \setlength\belowcaptionskip{2\p@}%
                \@dblfloat{table}}
               {\end@dblfloat}

\long\def\@caption#1[#2]#3{\par\addcontentsline{\csname
  ext@#1\endcsname}{#1}{\protect\numberline{\csname
  the#1\endcsname}{\ignorespaces #2}}\begingroup
    \@parboxrestore
    \@makecaption{\csname fnum@#1\endcsname}{\ignorespaces #3}\par
  \endgroup}


\def\institute#1{\gdef\@institute{#1}}

\def\institutename{\if!\@institute!\relax%
\else\par
 \begingroup
 \parskip=\z@
 \parindent=\z@
 \putcounter{@inst}{1}%
 \def\and{\par\stepcounter{@inst}%
          \noindent
          \llap{\hbox to\instindent{\hss$^{\@instsymbol\the@inst}$}}\ignorespaces}%
 \setbox0=\vbox{\def\thanks##1{}\def\deceased##1{}\@institute}%
 \ifnum\value{@inst}>9\relax\setbox0=\hbox{$^{88}$}%
                 \else\setbox0=\hbox{$^{8}$}\fi
 \center\instindent=\wd0
 \leftskip=\wd0\relax
 \ifnum\value{@inst}=1\relax
   \leftskip=\z@
 \else
   \putcounter{footnote}{\c@@inst}%
   \putcounter{@inst}{1}%
   \vskip-6pt\centering\noindent
   \llap{\hbox to\instindent{\hss$^{\@instsymbol\the@inst}$}}\ignorespaces
 \fi
 \ignorespaces
 \centering\itshape\@institute\par
 \endgroup\fi}

\def\@fnsymbol#1{\ensuremath{\ifcase#1\or\ast\or{\ast\ast}\or
   {\ast\ast\ast}\or %\dagger
   {\ast\ast\ast\ast}\or \ddagger\or
   \mathchar "278\or \mathchar "27B\or \|\or **\or \dagger\dagger
   \or \ddagger\ddagger \else\@ctrerr\fi}}

\def\@instsymbol#1{\ensuremath{\ifcase#1\or\ast\or{\ast\ast}\or
   {\ast\ast\ast}\or %\dagger
   {\ast\ast\ast\ast}\or \ddagger\or
   \mathchar "278\or \mathchar "27B\or \|\or **\or \dagger\dagger
   \or \ddagger\ddagger \else\@ctrerr\fi}}

\def\inst#1{\unskip$^{\@instsymbol#1}$}
\def\fnmsep{\unskip$^,$}

\def\subtitle#1{\gdef\@subtitle{#1}}
\clearheadinfo

\renewcommand\maketitle{\par\pagestyle{headings}
\iflanguage{russian}{%
\def\algorithmname{\selectlanguage{russian}\CYRA\cyrl\cyrg\cyro\cyrr\cyri\cyrt\cyrm}
\def\assertionname{\selectlanguage{russian}\CYRU\cyrt\cyrv\cyre\cyrr\cyrzh\cyrd\cyre\cyrn\cyri\cyre}
\def\assumptionname{\selectlanguage{russian}\CYRP\cyrr\cyre\cyrd\cyrp\cyro\cyrl\cyro\cyrzh\cyre\cyrn\cyri\cyre}
\def\casename{\selectlanguage{russian}\CYRS\cyrl\cyru\cyrch\cyra\cyrishrt}
\def\corollaryname{\selectlanguage{russian}\CYRS\cyrl\cyre\cyrd\cyrs\cyrt\cyrv\cyri\cyre}
\def\definitionname{\selectlanguage{russian}\CYRO\cyrp\cyrr\cyre\cyrd\cyre\cyrl\cyre\cyrn\cyri\cyre}
\def\examplename{\selectlanguage{russian}\CYRP\cyrr\cyri\cyrm\cyre\cyrr}
\def\exercisename{\selectlanguage{russian}\CYRU\cyrp\cyrr\cyra\cyrzh\cyrn\cyre\cyrn\cyri\cyre}
\def\keywordname{\selectlanguage{russian}\CYRK\cyrl\cyryu\cyrch\cyre\cyrv\cyrery\cyre%
\ \cyrs\cyrl\cyro\cyrv\cyra}
\def\lemmaname{\selectlanguage{russian}\CYRL\cyre\cyrm\cyrm\cyra}
\def\notename{\selectlanguage{russian}\CYRZ\cyra\cyrm\cyre\cyrch\cyra\cyrn\cyri\cyre}
\def\problemname{\selectlanguage{russian}\CYRZ\cyra\cyrd\cyra\cyrch\cyra}
\def\proofname{\selectlanguage{russian}\CYRD\cyro\cyrk\cyra\cyrz\cyra\cyrt\cyre\cyrl\cyrsftsn\cyrs\cyrt\cyrv\cyro}
\def\propositionname{\selectlanguage{russian}\CYRP\cyrr\cyre\cyrd\cyrl\cyro\cyrzh\cyre\cyrn\cyri\cyre}
\def\questionname{\selectlanguage{russian}\CYRV\cyro\cyrp\cyrr\cyro\cyrs}
\def\remarkname{\selectlanguage{russian}\CYRZ\cyra\cyrm\cyre\cyrch\cyra\cyrn\cyri\cyre}
\def\solutionname{\selectlanguage{russian}\CYRR\cyre\cyrsh\cyre\cyrn\cyri\cyre}
\def\stagename{\selectlanguage{russian}\CYREREV\cyrt\cyra\cyrp}
\def\stepname{\selectlanguage{russian}\CYRSH\cyra\cyrg}
\def\theoremname{\selectlanguage{russian}\CYRT\cyre\cyro\cyrr\cyre\cyrm\cyra}
\@ifundefined{thechapter}%
{\def\contentsname{{\selectlanguage{russian}\CYRS\cyro\cyrd\cyre\cyrr\cyrzh\cyra\cyrn\cyri\cyre}}}%
{\def\contentsname{{\selectlanguage{russian}\CYRO\cyrg\cyrl\cyra\cyrv\cyrl\cyre\cyrn\cyri\cyre}}}%
\def\abstractname{{\selectlanguage{russian}\CYRA\cyrn\cyrn\cyro\cyrt\cyra\cyrc\cyri\cyrya}}%
\def\alsoname{{\selectlanguage{russian}\cyrs\cyrm.\ \cyrt\cyra\cyrk\cyrzh\cyre}}%
\def\appendixname{{\selectlanguage{russian}\CYRP\cyrr\cyri\cyrl\cyro\cyrzh\cyre\cyrn\cyri\cyre}}%
\def\authorname{{\selectlanguage{russian}\CYRI\cyrm\cyre\cyrn\cyrn\cyro\cyrishrt%
\ \cyru\cyrk\cyra\cyrz\cyra\cyrt\cyre\cyrl\cyrsftsn}}%
\def\bibname{{\selectlanguage{russian}\CYRL\cyri\cyrt\cyre\cyrr\cyra\cyrt\cyru\cyrr\cyra}}%
\def\ccname{{\selectlanguage{russian}\cyri\cyrs\cyrh.}}%
\def\chaptername{{\selectlanguage{russian}\CYRG\cyrl\cyra\cyrv\cyra}}%
\def\enclname{{\selectlanguage{russian}\cyrv\cyrk\cyrl.}}%
\def\figurename{{\selectlanguage{russian}\CYRR\cyri\cyrs.}}%
\def\headtoname{{\selectlanguage{russian}\cyrv\cyrh.}}%
\def\indexname{{\selectlanguage{russian}\CYRP\cyrr\cyre\cyrd\cyrm\cyre\cyrt\cyrn\cyrery\cyrishrt%
\ \cyru\cyrk\cyra\cyrz\cyra\cyrt\cyre\cyrl\cyrsftsn}}%
\def\listfigurename{{\selectlanguage{russian}\CYRS\cyrp\cyri\cyrs\cyro\cyrk%
\ \cyri\cyrl\cyrl\cyryu\cyrs\cyrt\cyrr\cyra\cyrc\cyri\cyrishrt}}%
\def\listtablename{{\selectlanguage{russian}\CYRS\cyrp\cyri\cyrs\cyro\cyrk%
\ \cyrt\cyra\cyrb\cyrl\cyri\cyrc}}%
\def\pagename{{\selectlanguage{russian}\cyrs.}}%
\def\partname{{\selectlanguage{russian}\CYRCH\cyra\cyrs\cyrt\cyrsftsn}}%
\def\prefacename{{\selectlanguage{russian}\CYRP\cyrr\cyre\cyrd\cyri\cyrs\cyrl\cyro\cyrv\cyri\cyre}}%
\def\refname{{\selectlanguage{russian}\CYRS\cyrp\cyri\cyrs\cyro\cyrk%
\ \cyrl\cyri\cyrt\cyre\cyrr\cyra\cyrt\cyru\cyrr\cyrery}}%
\def\seename{{\selectlanguage{russian}\cyrs\cyrm.}}%
\def\tablename{{\selectlanguage{russian}\CYRT\cyra\cyrb\cyrl\cyri\cyrc\cyra}}%
}{%
\def\algorithmname{Algorithm}
\def\assertionname{Assertion}
\def\assumptionname{Assumption}
\def\casename{Case}
\def\corollaryname{Corollary}
\def\definitionname{Definition}
\def\examplename{Example}
\def\exercisename{Exercise}
\def\keywordname{Key words}
\def\lemmaname{Lemma}
\def\notename{Note}
\def\problemname{Problem}
\def\proofname{Proof}
\def\propositionname{Proposition}
\def\questionname{Question}
\def\remarkname{Remark}
\def\solutionname{Solution}
\def\stagename{Stage}
\def\stepname{Step}
\def\theoremname{Theorem}
\def\abstractname{Abstract}%
\def\alsoname{see also}%
\def\appendixname{Appendix}%
\def\bibname{Bibliography}%
\def\ccname{cc}%
\def\chaptername{Chapter}%
\def\contentsname{Contents}%
\def\enclname{encl}%
\def\figurename{Figure}%
\def\headtoname{To}%
\def\indexname{Index}%
\def\listfigurename{List of Figures}%
\def\listtablename{List of Tables}%
\def\pagename{Page}%
\def\partname{Part}%
\def\prefacename{Preface}%
\def\refname{References}%
\def\seename{see}%
\def\tablename{Table}%
}%



  \@definecounter{section}
  \@addtoreset{subsection}{section}
  \@addtoreset{stheorem}{section}
  \@addtoreset{scorollary}{section}
  \@addtoreset{sdefinition}{section}
  \@addtoreset{sexample}{section}
  \@addtoreset{sexercise}{section}
  \@addtoreset{slemma}{section}
  \@addtoreset{snote}{section}
  \@addtoreset{sproblem}{section}
  \@addtoreset{sproposition}{section}
  \@addtoreset{squestion}{section}
  \@addtoreset{sremark}{section}
  \@addtoreset{ssolution}{section}
  \@addtoreset{sassertion}{section}
%
  \stepcounter{chapter}%
  \addtocounter{chapter}{-1}%
  \stepcounter{section}%
  \putcounter{section}{0}%
  \putcounter{subsection}{0}%
  \putcounter{figure}{0}%
  \putcounter{table}{0}%
  \putcounter{equation}{0}%
  \putcounter{theorem}{0}%
  \putcounter{corollary}{0}%
  \putcounter{definition}{0}%
  \putcounter{example}{0}%
  \putcounter{exercise}{0}%
  \putcounter{lemma}{0}%
  \putcounter{note}{0}%
  \putcounter{problem}{0}%
  \putcounter{proposition}{0}%
  \putcounter{question}{0}%
  \putcounter{remark}{0}%
  \putcounter{solution}{0}%
  \putcounter{footnote}{0}%
  \putcounter{assertion}{0}%
  \putcounter{assumption}{0}
  \putcounter{algorithm}{0}
  \putcounter{case}{0}
  \putcounter{stage}{0}
  \putcounter{step}{0}
  \putcounter{sassertion}{0}%
  \putcounter{stheorem}{0}%
  \putcounter{scorollary}{0}%
  \putcounter{sdefinition}{0}%
  \putcounter{sexample}{0}%
  \putcounter{sexercise}{0}%
  \putcounter{slemma}{0}%
  \putcounter{snote}{0}%
  \putcounter{sproblem}{0}%
  \putcounter{sproposition}{0}%
  \putcounter{squestion}{0}%
  \putcounter{ssolution}{0}%
  \putcounter{sassumption}{0}
  \putcounter{salgorithm}{0}
  \putcounter{scase}{0}
  \putcounter{sstage}{0}
  \putcounter{sstep}{0}
%
  \def\thesection{\@arabic\c@section}
  \def\theequation{\@arabic\c@equation}
  \begingroup
    \parindent=\z@
%    \renewcommand\thefootnote{\@fnsymbol\c@footnote}%
    \if@twocolumn
      \ifnum \col@number=\@ne
        \@maketitle
      \else
        \twocolumn[\@maketitle]%
      \fi
    \else
      \newpage
      \global\@topnum\z@   % Prevents figures from going at top of page.
      \@maketitle
    \fi
\thispagestyle{myheadings} \@thanks\@deceased

%
    \def\\{\unskip\ \ignorespaces}\def\inst##1{\unskip{}}%
    \def\thanks##1{\unskip{}}\def\deceased##1{\unskip{}}\def\fnmsep{\unskip}%
    \instindent=\hsize
    \advance\instindent by-\headlineindent
    \if!\@titlerunning!\else
      \def\@title{\@titlerunning}%
    \fi
    \global\setbox\titrun=\hbox{\small\rm\unboldmath\ignorespaces\MakeTextUppercase{\@title}}%
    \ifdim\wd\titrun>\instindent
       \typeout{Title too long for running head. Please supply}%
       \typeout{a shorter form with \string\titlerunning\space prior to
                \string\maketitle}%
       \global\setbox\titrun=\hbox{\small\rm
       !!! \cyrn\cyra\cyrz\cyrv\cyra\cyrn\cyri\cyre\ \cyrs\cyrt\cyra\cyrt\cyrsftsn\cyri\ \cyrs\cyrl\cyri\cyrsh\cyrk\cyro\cyrm\ \cyrd\cyrl\cyri\cyrn\cyrn\cyro\cyre\ \cyrd\cyrl\cyrya\ \cyrv\cyre\cyrr\cyrh\cyrn\cyre\cyrg\cyro\ \cyrk\cyro\cyrl\cyro\cyrn\cyrt\cyri\cyrt\cyru\cyrl\cyra\ !!!}%
    \fi
    \xdef\@title{\copy\titrun}%
%
       \if!\@authorrunning!%
            \value{@inst}=\value{@auth}%
            \putcounter{@auth}{1}%
       \else
           \edef\@author{\@authorrunning}%
            \fi%
        \global\setbox\authrun=\hbox{\small\unboldmath\@author\unskip}%
         \ifdim\wd\authrun>\instindent
          \typeout{Names of authors too long for running head. Please supply}%
          \typeout{a shorter form with \string\authorrunning\space prior to
                   \string\maketitle}%
          \global\setbox\authrun=\hbox{\small\rm
          Authors Suppressed Due to Excessive Length}%
       \fi
       \xdef\@author{\copy\authrun}%
       \markboth{\@author}{\@title}%
  \endgroup
  \putcounter{footnote}{0}%
  \clearheadinfo}
%
\def\@maketitle{\newpage
\markboth{}{}%
\def\lastand{\ifnum\value{@inst}=2\relax
                 \unskip{} \andname\
              \else
                 \unskip \lastandname\
              \fi}%
 \def\and{\stepcounter{@auth}\relax
          \ifnum\value{@auth}=\value{@inst}%
             \lastand
          \else
             \unskip,
          \fi}%
  \center
   {\LARGE\bfseries\boldmath
  \pretolerance=10000
  \settoheight{\ArticleHeadDepth}{X}
  \addtolength{\ArticleHeadDepth}{-\headsep}
  \addtolength{\ArticleHeadDepth}{32.4pt}
  \mbox{}\vskip\ArticleHeadDepth
%\@saltykov\par
\@deceased\par
  \@title
  \par}\vskip 10pt
\if!\@subtitle!\else {\Large\bfseries\boldmath
  \vskip -.65cm
  \pretolerance=10000
  \@subtitle \par}\vskip .8cm\fi
 \setbox0=\vbox{\putcounter{@auth}{1}\def\and{\stepcounter{@auth}}%
 \def\thanks##1{}\def\deceased##1{}\@author}%
 \global\value{@inst}=\value{@auth}%
 \global\value{auco}=\value{@auth}%
 \putcounter{@auth}{1}%
 {\lineskip .5em\noindent\ignorespaces {\large\bfseries\@author}\vskip2pt}
 {\small\institutename}
 {\footnotesize\@received\par}%
\gdef\@deceased{}
 }

% redefinition of the "\newtheorem" command.
%
% Usage now:
%
%     \newtheorem{env_nam}{caption}[within]{cap_font}{body_font}
% or  \newtheorem{env_nam}[numbered_like]{caption}{cap_font}{body_font}
% or  \newtheorem*{env_nam}{caption}{cap_font}{body_font}
%
% New is "cap_font" and "body_font". It stands for
% fontdefinition of the caption and the text itself.
%
% "\newtheorem*" gives a theorem without number.
%
% A defined newthoerem environment is used as described
% by Lamport.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\def\@thmcountersep{}
\def\@thmcounterend{.}

\def\newtheorem{\@ifstar{\@sthm}{\@Sthm}}

% definition of \newtheorem with number

\def\@Sthm#1{\@ifnextchar[{\@othm{#1}}{\@nthm{#1}}}

\def\@xnthm#1#2[#3]#4#5{%\expandafter\@ifdefinable\csname #1\endcsname
   {\@definecounter{#1}\@addtoreset{#1}{#3}%
   \expandafter\xdef\csname the#1\endcsname{\expandafter\noexpand
     \csname the#3\endcsname \noexpand\@thmcountersep \@thmcounter{#1}}%
   \expandafter\xdef\csname #1name\endcsname{#2}%
   \global\@namedef{#1}{\@thm{#1}{\csname #1name\endcsname}{#4}{#5}}%
                              \global\@namedef{end#1}{\@endtheorem}}}

\def\@ynthm#1#2#3#4{%\expandafter\@ifdefinable\csname #1\endcsname
   {\@definecounter{#1}%
   \expandafter\xdef\csname the#1\endcsname{\@thmcounter{#1}}%
   \expandafter\xdef\csname #1name\endcsname{#2}%
   \global\@namedef{#1}{\@thm{#1}{\csname #1name\endcsname}{#3}{#4}}%
                               \global\@namedef{end#1}{\@endtheorem}}}

\def\@othm#1[#2]#3#4#5{%
  \@ifundefined{c@#2}{\@latexerr{No theorem environment `#2' defined}\@eha}%
  {\expandafter\@ifdefinable\csname #1\endcsname
  {\global\@namedef{the#1}{\@nameuse{the#2}}%
  \expandafter\xdef\csname #1name\endcsname{#3}%
  \global\@namedef{#1}{\@thm{#2}{\csname #1name\endcsname}{#4}{#5}}%
  \global\@namedef{end#1}{\@endtheorem}}}}

\def\@thm#1#2#3#4{\refstepcounter
    {#1}\@ifnextchar[{\@ythm{#1}{#2}{#3}{#4}}{\@xthm{#1}{#2}{#3}{#4}}}

\def\@xthm#1#2#3#4{\@begintheorem{#2}{\csname the#1\endcsname}{#3}{#4}%
                    \ignorespaces}

\def\@ythm#1#2#3#4[#5]{\@opargbegintheorem{#2}{\csname
       the#1\endcsname}{#5}{#3}{#4}\ignorespaces}

\def\@begintheorem#1#2#3#4{\trivlist
                 \item[\hskip\labelsep\hskip\leftmargini{#3#1\ #2\@thmcounterend}]#4}

\def\@opargbegintheorem#1#2#3#4#5{\trivlist
      \item[\hskip\labelsep\hskip\leftmargini{#4#1\ #2\ \normalfont(#3)\@thmcounterend}]#5}

% definition of \newtheorem* without number

\def\@sthm#1#2{\@Ynthm{#1}{#2}}

\def\@Ynthm#1#2#3#4{\expandafter\@ifdefinable\csname #1\endcsname
   {\global\@namedef{#1}{\@Thm{\csname #1name\endcsname}{#3}{#4}}%
    \expandafter\xdef\csname #1name\endcsname{#2}%
    \global\@namedef{end#1}{\@endtheorem}}}

\def\@Thm#1#2#3{\@ifnextchar[{\@Ythm{#1}{#2}{#3}}{\@Xthm{#1}{#2}{#3}}}

\def\@Xthm#1#2#3{\@Begintheorem{#1}{#2}{#3}\ignorespaces}

\def\@Ythm#1#2#3[#4]{\@Opargbegintheorem{#1}
       {#4}{#2}{#3}\ignorespaces}

\def\@Begintheorem#1#2#3{#3\trivlist
                           \item[\hskip\labelsep\hskip\leftmargini{\normalfont#2#1\@thmcounterend}]}

\def\@Opargbegintheorem#1#2#3#4{#4\trivlist
      \item[\hskip\labelsep\hskip\leftmargini{#3#1\ \normalfont(#2)\@thmcounterend}]}

% definition of divers theorem environments

\if@envcntsect
   \def\@thmcountersep{.}
   \newtheorem{theorem}{Theorem}[section]{\bfseries}{\itshape}
\else
   \newtheorem{theorem}{Theorem}{\bfseries}{\itshape}
   \if@envcntreset
      \@addtoreset{theorem}{section}
   \else
      \@addtoreset{theorem}{chapter}
   \fi
\fi


\newtheorem*{assumption*}{\noexpand\assumptionname}{\bfseries}{\itshape}
\newtheorem*{corollary*}{\noexpand\corollaryname}{\bfseries}{\itshape}
\newtheorem*{definition*}{\noexpand\definitionname}{\bfseries}{\rm}
\newtheorem*{example*}{\noexpand\examplename}{\bfseries}{\rm}
\newtheorem*{lemma*}{\noexpand\lemmaname}{\bfseries}{\itshape}
\newtheorem*{proof}{\noexpand\proofname}{\bfseries}{\rm}
\newtheorem*{proposition*}{\noexpand\propositionname}{\bfseries}{\itshape}
\newtheorem*{remark*}{\noexpand\remarkname}{\itshape}{\rm}%{\bfseries}{\rm}
\newtheorem*{solution*}{\noexpand\solutionname}{\itshape}{\rm}
\newtheorem*{theorem*}{\noexpand\theoremname}{\bfseries}{\itshape}
\newtheorem{algorithm}{\noexpand\algorithmname}{\bfseries}{\rm}
\newtheorem{assertion}{\noexpand\assertionname}{\bfseries}{\itshape}
\newtheorem{assumption}{\noexpand\assumptionname}{\bfseries}{\itshape}
\newtheorem{case}{\noexpand\casename}{\bfseries}{\rm}
\newtheorem{corollary}{\noexpand\corollaryname}{\bfseries}{\itshape}
\newtheorem{definition}{\noexpand\definitionname}{\bfseries}{\rm}
\newtheorem{example}{\noexpand\examplename}{\bfseries}{\rm}
\newtheorem{exercise}{\noexpand\exercisename}{\itshape}{\rm}
\newtheorem{lemma}{\noexpand\lemmaname}{\bfseries}{\itshape}
\newtheorem{note}{\noexpand\notename}{\itshape}{\rm}
\newtheorem{problem}{\noexpand\problemname}{\itshape}{\rm}
\newtheorem{proposition}{\noexpand\propositionname}{\bfseries}{\itshape}
\newtheorem{question}{\noexpand\questionname}{\itshape}{\rm}
\newtheorem{remark}{\noexpand\remarkname}{\itshape}{\rm}%{\bfseries}{\rm}
\newtheorem{solution}{\noexpand\solutionname}{\itshape}{\rm}
\newtheorem{stage}{\noexpand\stagename}{\bfseries}{\rm}
\newtheorem{step}{\noexpand\stepname}{\bfseries}{\rm}


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%\numberwithin{equation}{section}
\newtheorem{salgorithm}{\noexpand\algorithmname}{\bfseries}{\rm}
\newtheorem{sassertion}{\noexpand\assertionname}{\bfseries}{\itshape}
\newtheorem{sassumption}{\noexpand\assumptionname}{\bfseries}{\itshape}
\newtheorem{scase}{\noexpand\casename}{\bfseries}{\rm}
\newtheorem{scorollary}{\noexpand\corollaryname}[section]{\bfseries}{\itshape}
\newtheorem{sdefinition}{\noexpand\definitionname}[section]{\bfseries}{\rm}
\newtheorem{sexample}{\noexpand\examplename}[section]{\bfseries}{\rm}
\newtheorem{sexercise}{\noexpand\exercisename}[section]{\itshape}{\rm}
\newtheorem{slemma}{\noexpand\lemmaname}[section]{\bfseries}{\itshape}
\newtheorem{snote}{\noexpand\notename}[section]{\itshape}{\rm}
\newtheorem{sproblem}{\noexpand\problemname}[section]{\itshape}{\rm}
\newtheorem{sproposition}{\noexpand\propositionname}[section]{\bfseries}{\itshape}
\newtheorem{squestion}{\noexpand\questionname}[section]{\itshape}{\rm}
\newtheorem{sremark}{\noexpand\remarkname}[section]{\itshape}{\rm}
\newtheorem{ssolution}{\noexpand\solutionname}[section]{\itshape}{\rm}
\newtheorem{sstage}{\noexpand\stagename}{\bfseries}{\rm}
\newtheorem{sstep}{\noexpand\stepname}{\bfseries}{\rm}
\newtheorem{stheorem}{\noexpand\theoremname}[section]{\bfseries}{\itshape}

\def\thestheorem{\arabic{section}.\arabic{stheorem}}
\def\thescorollary{\arabic{section}.\arabic{scorollary}}
\def\thesdefinition{\arabic{section}.\arabic{sdefinition}}
\def\theslemma{\arabic{section}.\arabic{slemma}}

\def\@takefromreset#1#2{%
    \def\@tempa{#1}%
    \let\@tempd\@elt
    \def\@elt##1{%
        \def\@tempb{##1}%
        \ifx\@tempa\@tempb\else
            \@addtoreset{##1}{#2}%
        \fi}%
    \expandafter\expandafter\let\expandafter\@tempc\csname cl@#2\endcsname
    \expandafter\def\csname cl@#2\endcsname{}%
    \@tempc
    \let\@elt\@tempd}

\def\theopargself{\def\@opargbegintheorem##1##2##3##4##5{\trivlist
      \item[\hskip\labelsep\hskip\leftmargini{##4##1\ ##2\ ##3}]##5}
                  \def\@Opargbegintheorem##1##2##3##4{##4\trivlist
      \item[\hskip\labelsep\hskip\leftmargini{##3##1\ ##2}]}
      }

\renewenvironment{abstract}
      {\list{}{\setlength\leftmargin{2\leftmargini}\rightmargin\leftmargin\small
      \labelwidth=\z@
      \listparindent=\z@
      \labelsep=\z@
      \itemindent\listparindent}%
                \item[\hskip\labelsep
                     \bfseries\abstractname---\ignorespaces]}
               {\endlist}

\newenvironment{keywords}
      {\list{}{\setlength\leftmargin{2\leftmargini}\rightmargin\leftmargin\small
      \labelwidth=\z@
      \listparindent=\z@
      \labelsep=\z@
      \itemindent\listparindent}%
                \item[\hskip\labelsep
                     \itshape\keywordname]\itshape}
               {\endlist}



\newdimen\headlineindent             % dimension for space between
\headlineindent=1.166cm              % number and text of headings.

\def\ps@headings{\let\@mkboth\@gobbletwo
\def\@oddfoot{\hbox to \textwidth{\noexpand\rmfamily\footnotesize\mbox{}\hskip\leftmargini\selectlanguage{russian}\CYRI\CYRN\CYRF\CYRO\CYRR\CYRM\CYRA\CYRC\CYRI\CYRO\CYRN\CYRN\CYRERY\CYRE\ \CYRP\CYRR\CYRO\CYRC\CYRE\CYRS\CYRS\CYRERY\quad{}%
\CYRT\CYRO\CYRM{} \@VolumeNo\quad{}\textnumero{} \@IssueNo\quad{}\@YearOfIssue\hfil}}%
\def\@evenfoot{\hbox to \textwidth{\noexpand\rmfamily\footnotesize\hfil\selectlanguage{russian}\CYRI\CYRN\CYRF\CYRO\CYRR\CYRM\CYRA\CYRC\CYRI\CYRO\CYRN\CYRN\CYRERY\CYRE\ \CYRP\CYRR\CYRO\CYRC\CYRE\CYRS\CYRS\CYRERY\quad{}%
\CYRT\CYRO\CYRM{} \@VolumeNo\quad{}\textnumero{} \@IssueNo\quad{}\@YearOfIssue\hskip\leftmargini}}%

\def\@evenhead{\hbox to \textwidth{\noexpand\rmfamily\small\thepage\hfil\leftmark\hfil\hphantom{\thepage}}}%
\def\@oddhead{\hbox to \textwidth{\noexpand\rmfamily\small\hphantom{\thepage}\hfil\rightmark\hfil\thepage}}%

   \def\chaptermark##1{}%
   \def\sectionmark##1{}%
   \def\subsectionmark##1{}}

\ps@headings

\def\ps@contheadings{\let\@mkboth\@gobbletwo
\def\@oddfoot{}%
\def\@evenfoot{}%
\def\@evenhead{}%
\def\@oddhead{}%
\def\chaptermark##1{}%
\def\sectionmark##1{}%
\def\subsectionmark##1{}}


\def\ps@myheadings{\clearpage\settoheight{\@fhd}{\@ArticleDataHeadA}%
\addtolength{\FirstHeadDepth}{-\@fhd}%
\setbox0=\hbox to
\textwidth{\small\itshape\makebox[0pt][l]{%
\@ifundefined{r@Lastp@ge}{\@ArticleDataHeadA@}%
{\putcounter{l@stp@ge}{\l@stp@geref}\addtocounter{l@stp@ge}{-\value{page}}%
\ifnum\value{l@stp@ge}=0\@ArticleDataHeadA@\else\@ArticleDataHeadA\fi}}%
\raisebox{-\baselineskip}{\makebox[0pt][l]{\@ArticleDataHeadC}}%
\raisebox{-\FirstHeadDepth}{\makebox[\textwidth][c]%
{\setlength{\@restrub}{\textwidth}%
\settowidth{\@rubwidth}{\@Rubric}%
\addtolength{\@restrub}{-\@rubwidth}%
\addtolength{\@restrub}{-2em}%
\dblrulefill{.5\@restrub}\hfill
\@Rubric\hfill\dblrulefill{.5\@restrub}}%
}\hfill}
\let\@mkboth\@gobbletwo
\def\@evenfoot{\hbox to \textwidth{\footnotesize\hfil\@ArticleDataFoot\hfil}}
\def\@oddfoot{\hbox to \textwidth{\footnotesize\hfil\@ArticleDataFoot\hfil}}%
\def\@oddhead{\unhbox0}
\def\@evenhead{\unhbox0}
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\newcommand{\revred}[1]{
                      {\vspace*{4mm}
                      {\it \hspace*{-2mm}
                         This paper was recommended for publication by {#1},
                         a member of the  Editorial Board}
                      %\hfill
                          }\par
                     }

\def\chred{\revred}
\newcommand{\eqdef}{\stackrel{\rm def}{=}}

%  {\it   Remark, Exercise, Note, Problem, Question, Solution}


%\renewcommand{\bfdefault}{b}%{bx}
\renewcommand{\topfraction}{1}
\renewcommand{\bottomfraction}{1}
\renewcommand{\textfraction}{0}
%\renewcommand{\floatsep}{0mm}
%\numberwithin{equation}{section}


\def\deceased{%\gdef\@deceased
$^{\boldsymbol\dagger}$%
\renewcommand{\thefootnote}{$\dagger$}%
 \footnotetext{Deceased.}%
\renewcommand{\thefootnote}{\arabic{footnote}}%
%\putcounter{footnote}{0}
}

\def\squareforqed{\hbox{\rlap{$\sqcap$}$\sqcup$}}
\def\qed{\ifmmode\squareforqed\else{\unskip\nobreak\hfil
\penalty50\hskip1em\null\nobreak\hfil\squareforqed
\parfillskip=0pt\finalhyphendemerits=0\endgraf}\fi}


\endinput
