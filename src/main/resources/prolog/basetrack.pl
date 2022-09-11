% DATABASE: BASE TRACK

% E ---> External
% I ---> Internal

straight(id(1), startPointE(181, 113), endPointE(725, 113), startPointI(181, 170), endPointI(725, 170), end(725), direction(1)).
straight(id(3), startPointE(181, 453), endPointE(725, 453), startPointI(181, 396), endPointI(725, 396), end(181), direction(-1)).

%Direction ---> -1 forward or 1 backward
%SP ---> Start Point
%EP ---> End Point

turn(id(2), center(725, 283), startPointE(725, 113), startPointI(725, 170), endPointE(725, 453), endPointI(725, 396), direction(1), topLimit(175), bottomLimit(390)).
turn(id(4), center(181, 283), startPointE(181, 113), startPointI(181, 170), endPointE(181, 453), endPointI(181, 396), direction(-1), topLimit(175), bottomLimit(390)).


startingPoint(id(1), position(313, 115)).
startingPoint(id(2), position(293, 129)).
startingPoint(id(3), position(273, 142)).
startingPoint(id(4), position(253, 155)).

finalPosition(position(633, 272)).
finalPosition(position(533, 272)).
finalPosition(position(433, 272)).
finalPosition(position(333, 272)).