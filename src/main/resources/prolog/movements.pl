%x0 + v * t + 0.5 * acc * t^2
newPositionStraight(Coord, Velocity, Time, Acceleration, Direction, Np) :-
    pow(Time, 2, TimeSquared), Np is Coord + (((Velocity * Time + 0.5 * Acceleration * TimeSquared) / 40) * Direction).

newVelocityAcceleration(Speed, Acc, Time, Deg, Fuel, Ns) :- limitation(Deg, Fuel, Z), Temp is Speed + (Acc * Time), Ns is Temp - (Temp * Z).
newVelocityDeceleration(Speed, Ns) :- Ns is Speed * 0.95.

limitation(Deg, Fuel, Z) :- x(Deg, X), y(Fuel, Y), Z is (X + Y) / 6.
x(Deg, X) :- X is 1 - Deg.
y(Fuel, Y) :- Y is Fuel / 120.

pow(X, Esp, Y):-  pow(X, X, Esp, Y).
pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).