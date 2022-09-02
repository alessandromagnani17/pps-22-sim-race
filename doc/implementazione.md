## Implementazione 

In questo capitolo è presente una discussione delle scelte implementative effettuate dai membri del team di sviluppo.

### Programmazione funzionale

Dato il contesto di sviluppo di questo progetto si è cercato di utilizzare il più possibile il paradigma funzionale. Di seguito sono elencati alcuni aspetti ritenuti rilevanti per comprendendere come questo tipo di paradigma sia stato sfruttato.

#### For comprehension 
Questo costrutto è basato sulle monadi e risulta molto utile per aumentare la leggibilità del codice e limitare l'approccio imperativo. In questo progetto è stato utilizzato in accoppiata con i Task della libreria [Monix](https://monix.io/). Di seguito un metodo, estratto dalla classe `SimulationEngine`, che rappresenta un singolo step della simulazione:
```scala
override def simulationStep(): Task[Unit] =
  for
    _ <- moveCars()
    _ <- updateStanding()
    _ <- updateView()
    _ <- waitFor(speedManager._simulationSpeed)
    _ <- checkEnd()
  yield ()
```

#### Pattern matching
Questo meccanismo permette di eseguire un match fra un valore e un dato pattern, ha una sintassi particolarmente idiomatica ed è stato usato principlamente in due accezioni:
1. Nel modo classico, come fosse una sorta di switch di Java più potente. Ad esempio:
```scala
val alpha = direction match
    case Direction.Forward => 0
    case Direction.Backward => 180
```
2. Per decomporre un oggetto nelle parti che lo costituiscono. Ad esempio, è stato molto utile nella classe `CarsLoader`: dopo aver effettuato la query prolog per caricare tutte le macchine si hanno i parametri di una data macchina in una lista, ogni elemento di questa lista può essere selezionato in modo maggiormente comprensibile grazie a questo meccanismo come segue:
```scala
 private def mkCar(params: List[String], track: Track): Car = params match {
    case List(path, name, tyre, skills, maxSpeed, acceleration, actualSector, fuel, carColor) => 
      ...
```

#### Higher-order functions
Il meccanismo delle higher-order functions permette di implementare funzioni che prendono come input un'altra funzione (o in generale più di una) e/o ritornano una funzione. Questo permette di implementare in maniera molto semplice il pattern strategy ed aumenta il riutilizzo del codice. 
```scala 
private def updateParameter[E](sector: Sector, onStraight: () => E, onTurn: () => E): E = sector match
    case s: Straight => onStraight()
    case t: Turn => onTurn()
```

#### Option 
In programmazione funzionale la classe `Option` è utiizzata per rappresentare la possibile assenza di un valore, evitando di utilizzare il valore `null`. In questo progetto è stata utilizzata nella classe `ControllerModule`, l'esigenza era di rappresentare la presenza di un valore per la variabile `stopFuture` solo nel caso in cui la simulazione sia effettivamente stata fatta partire.
```scala 
private var stopFuture: Option[Cancelable] = None

override def notifyStart(): Unit = stopFuture = Some(
  ....
)
```

#### Either
Questo meccanismo è utilizzato per la gestione delle eccezioni, un valore di `Either[+A, +B]` rappresenta un valore che potrebbe assumere sia tipo `A` che tipo `B`. Questo rende particolarmente comodo la gestione delle eccezioni in quanto si può usare un valore di `Either[Throwable, A]`. Nel caso specifico è stato utilizzato nella `ControllerModule` dato che viene ritornato dal metodo `runAsync` dei Task di Monix. Una volta ottenuto un valore di questo tipo lo si può gestire con una partial function che esprime le computazioni da intraprendere nei due casi.

```scala
context.simulationEngine
    .simulationStep()
    .loopForever
    .runAsync {
      case Left(exp) => global.reportFailure(exp)
      case _ =>
    }
```


#### Type-members

#### Mix-in



