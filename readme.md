# Racing Simulator

## Autori

[Davide Domini](mailto:davide.domini@studio.unibo.it),
[Alessandro Magnani](mailto:alessandro.magnani18@studio.unibo.it),
[Andrea Matteucci](mailto:andrea.matteucci5@studio.unibo.it),
[Simone Montanari](mailto:simone.montanari14@studio.unibo.it)

## Indice

- [Introduzione](#introduzione)
- [Processo di sviluppo adottato](#processo-di-sviluppo-adottato)
- [Requisiti](#requisiti)
    - [Requisiti Business](#requisiti-business)
    - [Requisiti Utente](#requisiti-utente)
    - [Requisiti Funzionali](#requisiti-funzionali)
    - [Requisiti non Funzionali](#requisiti-non-funzionali)
    - [Requisiti di implementazione](#requisiti-di-implementazione)
- [Design architetturale](#design-architetturale)
- [Design di dettaglio](#design-di-dettaglio)

## Introduzione
// TODO 

## Processo di sviluppo adottato
// TODO

## Requisiti

### Requisiti Business
L'obiettivo del progetto è quello di sviluppare un simulatore di gare di Formula 1. Il simulatore comprenderà un'interfaccia grafica che permetterà all'utente di interagire con il sistema. L'applicativo permetterà di modificare alcuni parametri inerenti alla gara, nello specifico:
- Tipo di gomme usate;
- Velocità massima della vettura;
- Indici di abilità di un dato pilota.

Inoltre, una volta impostati tutti i parametri, verrà mostrata una nuova schermata che permette di visualizzare lo stato della simulazione e alcuni grafici di interesse. 

### Requisiti Utente
Di seguito sono riportati i requisiti visti nell'ottica di cosa può fare l'utente con l'applicativo.
- L'utente potrà visualizzare ed impostare i parametri legati alla simulazione;
- L'utente potrà visualizzare lo stato della gara mediante un'interfaccia 2D con vista dall'alto;
- L'utente potrà visualizzare la classifica real-time della gara;
- L'utente potrà visualizzare vari grafici che riassumono l'andamento della gara;
- L'utente potrà visualizzare lo stato di una singola vettura;
- L'utente potrà modificare la velocità della simulazione.

### Requisiti Funzionali
Di seguito sono riportati i requisiti individuati durante lo studio del dominio e le regole scelte per la sua rappresentazione.


- Il numero di vetture è fissato a 4 (`nCars`);
- Il numero di circuiti è fissato a 1 (`nCircuits`);
- Il numero di giri è impostabile nella schermata iniziale (`nLaps`);
- Ogni vettura sarà caratterizzata da: 
    - Gomme (`carTyres`), suddivise in:
        - Tipologia gomme (`carTyres.type`);
        - Usura delle gomme durante la gara (`carTyres.usury`);
    - Velocità della vettura (`carVelocity`), suddivisa in:
        - Velocità massima (`carVelocity.max`);
        - Velocità corrente (`carVelocity.current`);
    - Abilità di guida del pilota all'interno della vettura (`carDriverSkill`), suddivisa in:
        - Abilita di attacco (`carDriverSkill.attack`);
        - Abilita di difesa (`carDriverSkill.defense`);
- L'usura delle gomme di ogni vettura aumenterà di giro in giro considerando la tipologia di gomma usata;
- La velocità corrente di ogni vettura sarà calcolata considerando la velocità massima e l'usura delle gomme;
- Ogni qualvolta che due vetture si trovano ad una distanza minore o uguale a *X-da definire* metri, verrà eseguito l'algoritmo relativo al sorpasso per decretare quale vettura sarà davanti alla fine dell'iterazione. Nello specifico, l'algoritmo relativo ai sorpassi valuterà i seguenti parametri:
    - Attacco e difesa dei due piloti;
    - Velocità corrente delle due vetture;
    - Velocità massima delle due vetture.


### Requisiti non Funzionali
Di seguito sono descritti i requisiti non funzionali dell'applicativo:

* ***Usabilità***: L'interfaccia grafica dovrà essere semplice ed intuitiva così da permettere ad un utente non esperto del dominio di comprendere quali sono le principali operazioni che può eseguire;
* ***User Experience***: L'interfaccia grafica sarà implementata in modo da rendere piacevole l'esperienza dell'utente;
* ***Cross Platform***: Sarà possibile eseguire il sistema sui 3 principali sistemi operativi: Linux, Windows, MacOs.


### Requisiti di implementazione
Di seguito vengono riportati i requisiti relativi all'implementazione del sistema:

* Il sistema sarà sviluppato in Scala 3.* e per eventuali feature sarà possibile integrare delle teorie Prolog (?);
* Il sistema farà riferimento al JDK 11, eventuali librerie esterne utilizzate dovranno supportare almeno tale versione;
* Il testing del sistema sarà effettuato utilizzando ScalaTest, in questo modo sarà minimizzata la presenza di errori e facilitato l'aggiornamento di eventuali funzionalità;
* Il codice sorgente sarà verificato mediante l'utilizzo del linter ScalaFMT. (?)

## Design architetturale
// TODO

## Design di dettaglio
// TODO
