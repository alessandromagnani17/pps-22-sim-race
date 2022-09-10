## Processo di sviluppo adottato

Il processo di sviluppo adottato prende ispirazione, per la maggior parte, da SCRUM (un framework iterativo e incrementale per lo sviluppo di software) con qualche contaminazione anche da Extreme Programming (XP). Nel caso specifico si è preso ispirazione da SCRUM per:
- Organizzazione di riunioni periodiche: (1) Sprint Planning (2) Daily Scrum (3) Sprint review;
- Artefatti: Product Backlog;
- Task: si è tenuto traccia dei task svolti dai vari componenti del gruppo.

Per quanto riguarda XP, invece, è stato adottato un approccio di Pair Programming, ovvero in molte fasi di sviluppo si è programmato a coppie; in una coppia sono presenti due ruoli:
1. Driver, colui che effettivamente scrive il codice;
2. Navigator, colui che pensa all'approccio da adottare per implementare una data feature. 


### Sprint 
Per gli sprint è stata scelta la durata di una settimana. Questa decisione è stata presa in modo da poter avere prototipi incrementali con l'aggiunta di poche feature alla volta, questo permette di poter validare spesso le nuove feature e individuare con rapidità potenziali fraintendimenti fra team di sviluppo e cliente. L'unico sprint di durata diversa è stato il terzo: questo è durato due settimane per permettere ai componenti del gruppo di lavorare solo a tempo parziale e non a tempo pieno.

All'inizio di ogni sprint è stata effettuata una riunione di circa due ore in cui sono stati assegnati i task della settimana ai singoli componenti del gruppo e in cui sono state decise le coppie di programmatori. 

Alla fine di ogni sprint è stata effettuata una riunione di circa due ore in cui si è discusso di quanto implementato, unito le varie feature ed effettuato la release di un prototipo (tutte le release si possono trovare [a questo indirizzo](https://github.com/davidedomini/pps-22-sim-race/releases)).


### Product Backlog 

Il Product Backlog è un artefatto che contiene tutte le funzionalità necessarie per la realizzazione e il corretto funzionamento dell'applicativo. Questo documento viene aggiornato nel corso dei vari Sprint Planning considerando le esigenze che vengono riscontrate con l'avanzamento del progetto. La sua struttura prevede:
- I macro task;
- Gli item di cui si compone ogni macro task;
- I link utili;
- Un punteggio, da 1 a 10, che rappresenta la difficoltà prevista per portare a termine un determinato item;
- Una sezione in cui poter indicare un punteggio di difficoltà revisionato dopo aver portato a termine il dato item.

### Flusso di lavoro

Per ogni task da svolgere durante un dato sprint è stata definita una scheda su [Trello](https://trello.com/it) in modo da poter tenere traccia dello stato di avanzamento delle varie feature.

Per quanto riguarda l'organizzazione del [repository GitHub](https://github.com/davidedomini/pps-22-sim-race) si è scelto di adottare `Git Flow`. Questo prevede l'utilizzo di diversi branch:
- Un branch `main` in cui è presente il codice delle varie release;
- Un branch `develop` da usare come branch principale;
- Un branch `feature/<nome-feature>` in cui è presente il codice necessario per implementare una determinata feature.

Un riassunto molto chiaro di questa metodologia di lavoro è dato nella seguente immagine:
![Git-Flow Workflow](./imgs/gitflow.svg)

Infine, si è deciso di utilizzare anche la [Conventional Commit Specification](https://www.conventionalcommits.org/en/v1.0.0/), in modo da uniformare la struttura dei commit fra i vari membri del gruppo.

### Strumenti utilizzati

I vari strumenti di supporto al lavoro utilizzati sono:
- *SBT*, per la build automation;
- *Trello*, per la divisione dei task;
- *ScalaTest*, per la scrittura dei test;
- *ScalaFMT*, per la formattazione del codice;
- *GitHub Actions*, per la continuous integration;
  - É stato definito un file YAML per descrivere la pipeline: ad ogni `push` o `pull` sul branch `main` o `develop` l'applicativo viene compilato e testato su diversi sistemi operativi (Windows, Ubuntu e macOS) con JVM 11.
- *Swing*, per la realizzazione dell'interfaccia grafica.
