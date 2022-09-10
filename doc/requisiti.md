## Requisiti

### Requisiti Business
L'obiettivo del progetto è quello di sviluppare un simulatore di gare di Formula1. Il simulatore comprenderà un'interfaccia grafica che permetterà all'utente di interagire con il sistema. L'applicativo permetterà di modificare alcuni parametri inerenti la gara, nello specifico:
- Tipo di gomme usate;
- Velocità massima della vettura;
- Indice di abilità di un dato pilota.

Inoltre, una volta impostati tutti i parametri, verrà mostrata una nuova schermata che permette di visualizzare lo stato della simulazione e alcuni grafici di interesse. 

Una volta terminata la simulazione sarà possibile consultare una schermata riassuntiva e tornare alla schermata principale per effettuare nuove prove.

### Requisiti Utente
Di seguito sono riportati i requisiti visti nell'ottica di cosa può fare l'utente con l'applicativo.
- L'utente potrà visualizzare ed impostare i parametri legati alla simulazione;
- L'utente potrà visualizzare lo stato della gara mediante un'interfaccia 2D con vista dall'alto;
- L'utente potrà visualizzare la classifica real-time della gara;
- L'utente potrà visualizzare vari grafici che riassumono l'andamento della gara;
- L'utente potrà modificare la velocità della simulazione;
- L'utente potrà mettere in pausa e far ripartire la simulazione;
- L'utente potrà visualizzare la classifica con alcuni dati riassuntivi al termine della gara;
- L'utente potrà tornare al pannello principale per effettuare nuove simulazioni.

### Requisiti Funzionali
Di seguito sono riportati i requisiti individuati durante lo studio del dominio e le regole scelte per la sua rappresentazione.


- Il numero di vetture è fissato a 4;
- Il numero di circuiti è fissato a 1;
- Il numero di giri è impostabile nella schermata iniziale, potranno variare da un minimo di 1 ad un massimo di 30;
- Ogni vettura sarà caratterizzata da: 
    - Gomme, suddivise in:
        - Tipologia gomme;
        - Degrado delle gomme durante la gara;
    - Velocità della vettura, suddivisa in:
        - Velocità massima;
        - Velocità corrente;
    - Abilità di guida del pilota all'interno della vettura;
       
- L'usura delle gomme di ogni vettura aumenterà di giro in giro considerando la tipologia di gomma usata;
- La velocità corrente di ogni vettura sarà calcolata considerando la velocità massima, il carburante e l'usura delle gomme.



### Requisiti non Funzionali
Di seguito sono descritti i requisiti non funzionali dell'applicativo:

* ***Usabilità***: L'interfaccia grafica dovrà essere semplice ed intuitiva così da permettere ad un utente non esperto del dominio di comprendere quali sono le principali operazioni che può eseguire;
* ***User Experience***: L'interfaccia grafica sarà implementata in modo da rendere piacevole l'esperienza dell'utente;
* ***Cross Platform***: Sarà possibile eseguire il sistema sui 3 principali sistemi operativi: Linux, Windows, MacOs.


### Requisiti di implementazione
Di seguito vengono riportati i requisiti relativi all'implementazione del sistema:

* Il sistema sarà sviluppato in Scala 3.1.3 e per eventuali feature sarà possibile integrare delle teorie Prolog;
* Il sistema farà riferimento al JDK 11, eventuali librerie esterne utilizzate dovranno supportare almeno tale versione;
* Il testing del sistema sarà effettuato utilizzando ScalaTest, in questo modo sarà minimizzata la presenza di errori e facilitato l'aggiornamento di eventuali funzionalità;
* Il codice sorgente sarà verificato mediante l'utilizzo di ScalaFMT.
