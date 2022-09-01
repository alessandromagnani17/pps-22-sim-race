## Design di dettaglio

In questa sezione verrà analizzata nel dettaglio la struttura dell'applicativo andando a descrivere i singoli componenti e le relazioni fra loro.

### Component programming & Cake Pattern

Come descritto nella sezione precedente, si è deciso di utilizzare il pattern architetturale MVC. Per agevolare l'implementazione di questa scelta si è deciso di utilizzare il Cake Pattern, questo permette di iniettare le dipendenze fra i vari componenti in modo semplice e dichiarativo utilizzando aspetti avanzati della programmazione funzionale tra cui: *self-type*, *mix-in* e *type-members*. Nello specifico ogni componente che si desidera implementare deve avere cinque aspetti principali:
1. Un trait che definisce l'interfaccia del componente;
2. Un trait `Provider` che definisce il riferimento al componente tramite una singleton-like val;
3. Un type-member `Requirements` che definisce, in modo dichiarativo, le dipendenze di altri componenti di cui ha bisogno per svolgere i propri compiti (queste verranno mixed-in dai provider degli altri componenti in modo automatico);
4. Un trait `Component` che definisce l'implementazione effettiva del componente;
5. Un trait `Interface` che si occupa di aggregare gli altri elementi del modulo per renderlo effettivamente utilizzabile.

Un esempio di modulo view implementato utilizzando questo pattern è il seguente:

```scala
object ViewModule:

  trait View:
    def show(vitualTime: Int): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      private val gui = MonadicGui()
      def show(virtualTime: Int): Unit = gui render virtualTime

  trait Interface extends Provider with Component:
    self: Requirements =>
```

Sfruttando questo pattern avanzato si è dunque deciso di implementare quattro moduli: Model, View, Controller ed Engine. Le dipendenze fra i vari moduli sono le seguenti: 
- View -> Controller
- Controller -> Model, Engine, View
- Engine -> View, Model, Controller

Di seguito è riportata una sezione per la descrizione dettagliata di ogni modulo.


### Model
![Model](./imgs/cake-model.svg)

### View
![View](./imgs/cake-view.svg)

### Controller
![Controller](./imgs/cake-controller.svg)

### Engine
![Engine](./imgs/cake-engine.svg)

### Pattern utilizzati

#### Pimp my library

Pimp my library è un pattern che può essere utilizzato per aggiungere un nuovo metodo ad una classe senza modificare il suo codice, è molto utile quando tale classe viene da una libreria di terze parti e non si ha la possibilità di modificare il codice esistente. É stato usato per arricchire: *Interi*, *Tuple2*, *Option*, *HashMap* e *JPanel*. Un esempio esplicativo è il seguente, è sorta la necessità di avere un metodo per aggiungere una lista di elementi ad un pannello di classe `JPanel` senza scorrere esplicitamente tale lista, la soluzione dunque è stata arricchire la classe JPanel con il seguente metodo:
```scala
object RichJPanel:
    extension (p: JPanel)
      def addAll[E <: Component](elements: List[E]): Unit =
        elements.foreach(p.add(_))
```

#### Factory

#### Strategy

#### Singleton

#### Adapter

#### Builder
