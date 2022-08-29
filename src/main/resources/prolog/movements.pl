%x0 + v * t + 0.5 * acc * t^2

computeNewPositionForStraight(Coord, Vel, Time, Acc, I, Np) :- pow(Time, 2, TimeSquared), Np is Coord + (((Vel * Time + 0.5 * Acc * TimeSquared) / 40) * I).

computeNewVelocity(Speed, Acc, Time, Ns) :- Ns is Speed + (Acc * Time).

computeNewVelocityDeceleration(Speed, Acc, Time, Ns) :- Ns is (Speed + (Acc * Time)) * 0.9.

pow(X, Esp, Y):-  pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).