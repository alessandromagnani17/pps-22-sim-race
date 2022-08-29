car(path("/cars/0-hard.png"), name("Ferrari"),  tyre("Soft"), driver(1,1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Red")).

car(path("/cars/1-hard.png"), name("Mercedes"), tyre("Soft"), driver(1,1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Cyan")).

car(path("/cars/2-hard.png"), name("Red Bull"), tyre("Soft"), driver(1,1), maxSpeed(200), acceleration(2),
	actualSector(1), fuel(130), color("Blue")).

car(path("/cars/3-hard.png"), name("McLaren"), tyre("Soft"), driver(1,1), maxSpeed(200),  acceleration(2),
	actualSector(1), fuel(130), color("Green")).

%Query --> Get all cars params
%car(path(P), name(N), tyre(T), driver(A, D), maxSpeed(MS), acceleration(ACC), actualSector(ASe), fuel(F), color(C))