% DATABASE: BASE TRACK

% E ---> External
% I ---> Internal

%straight(id, x0_E, y0_E, x1_E, y1_E, x0_I, y0_I, x1_I, y1_I)
%straight(id(ID), startPointE(X0_E, Y0_E), endPointE(X1_E, Y1_E), startPointI(X0_I, Y0_I), endPointI(X1_I, Y1_I))
straight(id(1), startPointE(181, 113), endPointE(725, 113), startPointI(181, 170), endPointI(725, 170), direction(1)).
straight(id(3), startPointE(181, 453), endPointE(725, 453), startPointI(181, 396), endPointI(725, 396), direction(-1)).

%Direction ---> -1 or 1
%SP ---> Start Point
%EP ---> End Point

%turn(id, x_center, y_center, x_SP_E, y_SP_E, x_SP_I, y_SP_I, x_EP_E, y_EP_E, x_EP_I, y_EP_I,direction)
%Query: turn(id(ID), center(X, Y), startPointE(X0_E, Y0_E), startPointI(X0_I, YO_I), endPointE(X1_E, Y1_E), endPointI(X1_I, Y1_I), direction(D))
turn(id(2), center(725, 283), startPointE(725, 113), startPointI(725, 170), endPointE(725, 453), endPointI(725, 396), direction(1)).
turn(id(4), center(181, 283), startPointE(181, 113), startPointI(181, 170), endPointE(181, 453), endPointI(181, 396), direction(-1)).


%startingPoint(id, x_position, y_position)
%Query: startingPoint(id(ID), position(X, Y)).
startingPoint(id(1), position(313, 115)).
startingPoint(id(2), position(293, 129)).
startingPoint(id(3), position(273, 142)).
startingPoint(id(4), position(253, 155)).
