% DATABASE: BASE TRACK

% E ---> External
% I ---> Internal


% Straight(id, x0_E, y0_E, x1_E, y1_E, x0_I, y0_I, x1_I, y1_I)
Straight(1, 272, 170, 634, 170, 272, 226, 634, 226)
Straight(3, 272, 396, 634, 396, 272, 340, 634, 340)


%Direction ---> -1 or 1
%SP ---> Start Point
%EP ---> Etart Point

% Turn(id, x_SP_E, y_SP_E, x_SP_I, y_SP_I, x_EP_E, y_EP_E, x_EP_I, y_EP_I,direction)
Turn(2, 634, 283, 634, 170, 634, 226, 634, 396, 634, 340, 1)
Turn(4, 272, 283, 272, 170, 272, 226, 272, 396, 272, 340, -1)