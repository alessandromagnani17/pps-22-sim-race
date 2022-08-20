%x0 + v * t + 0.5 * acc * t^2

computeNewPositionForStraight(Coord, Vel, Time, Acc, Np) :- pow(Time, 2, TimeSquared), Np is Coord + ((Vel * Time + 0.5 * Acc * TimeSquared) / 20).

computeNewVelocity(Speed, Acc, Time, Ns) :- Ns is Speed + (Acc * Time).

pow(X, Esp, Y):-  pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).