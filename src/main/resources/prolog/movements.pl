%x0 + v * t + 0.5 * acc * t^2

computeNewPosition(Coord, Vel, Time, Acc, Np) :- pow(Time, 2, TimeSquared), Np is Coord + Vel * Time + 0.5 * Acc * TimeSquared.

pow(X, Esp, Y):-  pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).