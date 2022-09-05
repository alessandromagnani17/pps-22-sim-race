car(path("/cars/0-soft.png"), name("Ferrari"),  tyre("Soft"), driver(1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Ferrari")).

car(path("/cars/1-soft.png"), name("Mercedes"), tyre("Soft"), driver(1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Mercedes")).

car(path("/cars/2-soft.png"), name("Red Bull"), tyre("Soft"), driver(1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Red Bull")).

car(path("/cars/3-soft.png"), name("McLaren"), tyre("Soft"), driver(1), maxSpeed(200),  acceleration(2),
	actualSector(1), fuel(130), color("McLaren")).

%Query --> Get all cars params
%car(path(P), name(N), tyre(T), driver(A, D), maxSpeed(MS), acceleration(ACC), actualSector(ASe), fuel(F), color(C))