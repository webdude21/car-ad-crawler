(ns car-ad-crawler.autobg
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(def ^:dynamic *make-model-map*
  {"AC"            ["Други"]
   "Acura"         ["Integra", "Mdx", "Rdx", "Rl", "Rsx", "Slx", "Tl", "Tsx"]
   "Aixam"         ["400", "505", "600"]
   "Alfa Romeo"    ["145", "146", "147", "155", "156", "156 sportwagon", "159", "159 sportwagon", "164", "166", "33", "75", "76", "8C Competizione", "90", "Alfetta", "Brera", "Crosswagon q4", "Giulia", "Giulietta", "Gt", "Gtv", "MiTo", "Spider", "Sprint", "Stelvio", "Sud"]
   "Aro"           ["10", "24", "242", "243", "244", "246", "32", "320", "324", "328", "33"]
   "Asia"          ["Rocsta"]
   "Aston martin"  [".", "DBS", "Db7", "Db9", "Rapide", "V12 Vantage", "V8 Vantage", "Vanquish"]
   "Audi"          ["100", "200", "50", "60", "80", "90", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "Allroad", "Q2", "Q3", "Q5", "Q7", "R8", "Rs3", "Rs4", "Rs5", "Rs6", "Rs7", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "SQ5", "SQ7", "Tt"]
   "Austin"        ["Allegro", "Ambassador", "Maestro", "Maxi", "Metro", "Mg", "Mini", "Montego", "Princess"]
   "BMW"           ["1", "114", "116", "118", "120", "123", "125", "130", "135", "1500", "1600", "1602", "1800", "2", "2 Active Tourer", "2 Gran Tourer", "2000", "2002", "220 d", "225", "228", "230", "235", "240", "2800", "3", "315", "316", "318", "320", "323", "324", "325", "328", "330", "335", "3gt", "4", "420", "428", "430", "435", "5", "5 Gran Turismo", "501", "518", "520", "523", "524", "525", "528", "530", "535", "540", "545", "550", "5GT", "6", "628", "630", "633", "635", "640", "645", "650", "7", "700", "721", "723", "725", "728", "730", "732", "733", "735", "740", "745", "750", "760", "840", "850", "Izetta", "M", "M Coupе", "M135", "M2", "M3", "M4", "M5", "M6", "X1", "X3", "X4", "X5", "X6", "Z1", "Z3", "Z4", "Z8", "i3", "i8"]
   "Bentley"       ["Arnage", "Azure", "Bentayga", "Continental", "Continental gt", "Flying Spur", "GT Convertible", "Mulsanne", "T-series"]
   "Berliner"      ["Coupe"]
   "Bertone"       ["Freeclimber"]
   "Borgward"      ["Hansa"]
   "Brilliance"    ["BC3", "BS2 ", "BS4", "BS6", "Dolphin", "FRV", "FSV", "H220", "H230", "H320", "H330", "H530", "V3", "V5"]
   "Bugatti"       ["Chiron", "Veyron"]
   "Buick"         ["Electra", "Invicta", "Park avenue", "Regal", "Rendezvous", "Skylark", "Skyline"]
   "Cadillac"      ["Allante", "BLS", "Brougham", "Cts", "Deville", "Eldorado", "Escalade", "Fleetwood", "STS", "Seville", "Srx", "Xlr"]
   "Chevrolet"     ["Alero", "Astro", "Avalanche", "Aveo", "Beretta", "Blazer", "Camaro", "Caprice", "Captiva", "Cavalier", "Cobalt", "Colorado", "Corvette", "Cruze", "Epica", "Equinox", "Evanda", "Gmc", "Hhr", "Impala", "Kalos", "Lacetti", "Lumina", "Malibu", "Matiz", "Niva", "Nova", "Nubira", "Orlando", "Rezzo", "Silverado", "Spark", "Ssr", "Suburban", "Tacuma", "Tahoe", "Tracker", "Trailblazer", "Transsport", "Trax", "Volt"]
   "Chrysler"      ["300c", "300m", "Cherokee", "Crossfire", "Daytona", "Es", "Gr.voyager", "Grand cherokee", "Gts", "Interpid", "Lebaron", "Neon", "New yorker", "Pacifica", "Pt cruiser", "Saratoga", "Sebring", "Stratus", "Vision", "Voyager", "Wrangler"]
   "Citroen"       ["2cv", "Ax", "Axel", "Berlingo", "Bx", "C - Zero", "C-Crosser", "C-Elysee", "C1", "C15", "C2", "C3", "C3 Picasso", "C3 pluriel", "C4", "C4 AIRCROSS", "C4 Cactus", "C4 Picasso", "C5", "C6", "C8", "Cx", "DS3", "DS4", "DS5", "Ds", "Evasion", "Grand C4 Picasso", "Gsa", "Gx", "Ln", "Nemo", "Oltcit", "Saxo", "Visa", "Xantia", "Xm", "Xsara", "Xsara picasso", "Zx"]
   "Corvette"      ["C06 Convertible", "C06 Coupe", "Powa", "Z06"]
   "Dacia"         ["1100", "1300", "1304", "1307", "1310", "1350", "Dokker", "Duster", "Liberta", "Lodgy", "Logan", "Nova", "Pickup", "Sandero", "Solenza"]
   "Daewoo"        ["Ace", "Chairman", "Cielo", "Espero", "Evanda", "Fso", "Kalos", "Korando", "Lacetti", "Lanos", "Leganza", "Magnus", "Matiz", "Musso", "Nexia", "Nubira", "Prince", "Racer", "Rezzo", "Super", "Tacuma", "Tico"]
   "Daihatsu"      ["Applause", "Charade", "Charmant", "Copen", "Cuore", "Feroza", "Gran move", "Hijet", "Materia", "Move", "Rocky", "Sharade", "Sirion", "Taft", "Terios", "Trevis", "Wildcat", "Yrv"]
   "Daimler"       ["Double six", "Six", "Sovereign"]
   "Datsun"        ["Bluebird", "Cherry", "Stanza"]
   "Dkw"           ["F102"]
   "Dodge"         ["Avenger", "Caliber", "Caravan", "Challenger", "Charger", "Coronet", "Dakota", "Daytona", "Durango", "Interpid", "Journey", "Magnum", "Neon", "Nitro", "Ram", "Shadow", "Stealth", "Stratus", "Viper"]
   "Dr"            ["1", "2", "3", "5"]
   "Eagle"         ["Premire", "Talon", "Vision"]
   "FSO"           ["Polonez"]
   "Ferrari"       ["308", "348", "360 modena", "360 spider", "458 Italia", "488", "599", "812 Superfast", "California", "Enzo", "F12berlinetta", "F430", "F456m", "F575m maranello", "F612 scaglietti", "FF", "GTC4Lusso", "LaFerrari", "Mondial 8", "Testarossa"]
   "Fiat"          ["1100", "124", "125", "126", "127", "128", "131", "132", "1400", "1500", "1800", "500", "500L", "500X", "500Х", "600", "650", "750", "Albea", "Argenta", "Barchetta", "Bertone", "Brava", "Bravo", "Campagnola", "Cinquecento", "Coupe", "Croma", "Doblo", "Duna", "Fiorino", "Freemont", "Fullback", "Idea", "Linea", "Marea", "Multipla", "Palio", "Panda", "Punto", "Qubo", "Regata", "Ritmo", "Scudo", "Sedici", "Seicento", "Siena", "Stilo", "Strada", "Tempra", "Tipo", "Topolino", "Ulysse", "Uno"]
   "Ford"          ["12m", "15m", "17m", "20m", "Aerostar", "B-Max", "Bronco", "C-max", "Capri", "Connect", "Consul", "Cortina", "Cosworth", "Cougar", "Countur", "Courier", "Crown victoria", "EcoSport", "Ecoline", "Edge", "Escape", "Escort", "Everest", "Excursion", "Expedition", "Explorer", "F150", "F250", "F350", "F450", "F550", "F650", "F750", "Fiesta", "Flex", "Focus", "Fusion", "Galaxy", "Granada", "Ka", "Kuga", "Maverick", "Mondeo", "Mustang", "Orion", "Probe", "Puma", "Ranger", "Rs", "S-Max", "Scorpio", "Sierra", "Sportka", "Streetka", "Taunus", "Taurus", "Thunderbird", "Windstar", "Zephyr"]
   "GOUPIL"        ["GEM E2", "GEM E4", "GEM E6"]
   "Gaz"           ["469", "69"]
   "Geo"           ["Metro", "Prizm", "Storm", "Tracker"]
   "Gmc"           ["Acadia", "Envoy", "Jimmy", "Saturn", "Savana", "Sierra", "Sonoma", "Terrain", "Tracker", "Typhoon", "Yukon"]
   "Great Wall"    ["Voleex C10", "Voleex C20", "Voleex C30"]
   "Great wall"    []
   "Heinkel"       ["Тrojan"]
   "Honda"         ["Accord", "Cbr", "Cbx", "City", "Civic", "Civic ballade", "Concerto", "Cr-v", "Crosstour", "Crx", "Crz", "Element", "Elysion", "Fit", "Fr-v", "Hr-v", "Insight", "Integra", "Jazz", "Legend", "Logo", "Nsx", "Odyssey", "Passport", "Pilot", "Prelude", "Quintet", "Ridgeline", "S2000", "Shuttle", "Stream"]
   "Hummer"        ["H1", "H2", "H3"]
   "Hyundai"       ["Accent", "Atos", "Coupe", "Elantra", "Equus", "Excel", "Galloper", "Genesis", "Getz", "Grace", "Grandeur", "I10", "I20", "I30", "I40", "IX35", "IX55", "Ioniq", "Ix20", "Kona", "Landskape", "Lantra", "Matrix", "Pony", "Porter", "S", "Santa fe", "Santamo", "Sonata", "Sonica", "Stelar", "Tb", "Terracan", "Trajet", "Tucson", "Veloster ", "Veracruz", "Xg"]
   "Ifa"           ["F9"]
   "Infiniti"      ["Ex30", "Ex35", "Ex37", "Fx 30", "Fx 35", "Fx 37", "Fx 45", "Fx 50", "Fx45", "G", "G coupe", "G sedan", "I", "J", "M", "Q", "Q30", "Q45", "QX50", "QX70", "Qx", "Qx4"]
   "Innocenti"     ["Mini"]
   "Isuzu"         ["Amigo", "D-max", "Gemini", "Piazza", "Pickup", "Rodeo", "Tfs", "Trooper", "Vehi cross"]
   "Iveco"         ["Massive"]
   "Jaguar"        ["Daimler", "Daimler double six", "Daimler six", "F-PACE", "F-Type", "S-type", "Sovereign", "Super v8", "X-type", "XE", "Xf", "Xj", "Xjr", "Xjs", "Xjsc", "Xk8", "Xkr"]
   "Jeep"          ["Cherokee", "Commander", "Compass", "Grand cherokee", "Patriot", "Renegade", "Wrangler"]
   "Jpx"           ["Montez"]
   "Kia"           ["Avella delta", "Cadenza", "Carens", "Carnival", "Ceed", "Cerato", "Clarus", "Joecs", "Joyce", "Magentis", "Mohave", "Morning", "Niro", "Opirus", "Optima", "Picanto", "Pride", "Pro ceed", "Quoris", "Ray", "Retona", "Rio", "Sedona", "Sephia", "Shuma", "Sorento", "Soul", "Spectra", "Sportage", "Stinger", "Stonic", "Venga", "Visto", "X-Trek"]
   "Lada"          ["1200", "1300", "1500", "1600", "2101", "21011", "21012", "21013", "21015", "2102", "2103", "2104", "21043", "2105", "21051", "21053", "2106", "21061", "21063", "2107", "21074", "2108", "21083", "2109", "21093", "21099", "2110", "21213", "Granta", "Kalina", "Niva", "Nova", "Oka", "Priora", "Samara"]
   "Laforza"       ["Magnum"]
   "Lamborghini"   ["Aventador", "Countach", "Diablo", "Gallardo", "Huracan", "Murcielago", "Reventon", "Urus", "Veneno"]
   "Lancia"        ["A112", "Aurelia", "Beta", "Dedra", "Delta", "Flavia", "Fulvia", "Kappa", "Lybra", "Musa", "Phedra", "Prisma", "Thema", "Thesis", "Unior", "Y", "Y10", "Ypsilon", "Zeta"]
   "Land Rover"    ["Defender", "Discovery", "Freelander", "Range Rover Evoque", "Range Rover Sport", "Range rover"]
   "Landwind"      ["Jx6476da"]
   "Lexus"         ["CT200h", "Es", "Gs", "Gx470", "Is", "LC", "LFA", "LS", "Lx", "NX", "RC", "RX330", "Rx", "Rx300", "Rx350", "Rx400h", "Rx450", "Sc"]
   "Lifan"         ["LF1010", "LF320", "LF520", "LF620", "LF6361", "LF7130", "LF7160", "X60"]
   "Lincoln"       ["Continental", "Ls", "MKS", "Mark", "Mark Lt", "Mark lt", "Mkx", "Mkz", "Navigator", "Town car", "Zephyr"]
   "Lotus"         ["Elise", "Europe", "Evora", "Exige"]
   "Mahindra"      ["Armada", "Bolero", "Cl", "Commander", "Goa", "Marshall", "Scorpio"]
   "Maserati"      ["3200 gt", "Biturbo", "Coupe gt", "Ghibli", "GranCabrio", "GranTurismo", "Gransport", "Levante", "Quattroporte", "Spyder", "Zagato"]
   "Matra"         ["Murena", "Rancho"]
   "Maybach"       ["57", "62"]
   "Mazda"         ["121", "2", "3", "323", "5", "6", "626", "929", "B2200", "B2500", "B2600", "BT-50", "CX-5", "CX-7", "CX-9", "Demio", "Mpv", "Mx-3", "Mx-5", "Mx-6", "Premacy", "Rx-7", "Rx-8", "Tribute", "Xedos", "СХ-3"]
   "McLaren"       ["540C Coupe", "570S Coupe", "650S", "675LT", "F1", "MP4-12C", "P1"]
   "Mercedes-Benz" ["110", "111", "113", "114", "115", "116", "123", "124", "126", "126-260", "150", "170", "180", "190", "200", "220", "230", "240", "250", "260", "280", "290", "300", "320", "350", "380", "420", "450", "500", "560", "600", "A", "A 140", "A 150", "A 160", "A 170", "A 180", "A 190", "A 200", "A 210", "A 220", "A 250", "A45 AMG", "AMG GT", "AMG GT S", "Adenauer", "B", "B 150", "B 170", "B 180", "B 200", "C", "C 180", "C 200", "C 220", "C 230", "C 240", "C 250", "C 270", "C 280", "C 30 AMG", "C 300", "C 32 AMG", "C 320", "C 350", "C 36 AMG", "C 400", "C 43 AMG", "C 450 AMG", "C 55 AMG", "C 63 AMG", "CL", "CL 230", "CL 320", "CL 420", "CL 500", "CL 55 AMG", "CL 600", "CL 63 AMG", "CL 65 AMG", "CLA", "CLA 180", "CLA 200", "CLA 220", "CLA 250", "CLA 45 AMG", "CLC", "CLC 160", "CLC 180", "CLC 200", "CLC 220", "CLC 230", "CLC 250", "CLC 350", "CLK", "CLK 55 AMG", "CLK 63 AMG", "CLS", "CLS 250", "CLS 300", "CLS 320", "CLS 350", "CLS 500", "CLS 55", "CLS 55 AMG", "CLS 63", "CLS 63 AMG", "Citan", "E", "E 200", "E 220", "E 230", "E 240", "E 250", "E 260", "E 270", "E 280", "E 290", "E 300", "E 320", "E 350", "E 36 AMG", "E 400", "E 420", "E 43 AMG", "E 430", "E 50 AMG", "E 500", "E 55", "E 55 AMG", "E 60", "E 60 AMG", "E 63 AMG", "G", "G 230", "G 240", "G 250", "G 270", "G 280", "G 290", "G 300", "G 320", "G 350", "G 36 AMG", "G 400", "G 500", "G 55 AMG", "G 63 AMG", "G 65 AMG", "GL", "GL 320", "GL 350", "GL 420", "GL 450", "GL 500", "GL 55 AMG", "GL 63 AMG", "GLA", "GLA 180", "GLA 200", "GLA 220", "GLA 250", "GLA 45 AMG", "GLC 220", "GLC 250", "GLC 300", "GLC 63 AMG", "GLE 250", "GLE 350", "GLE 400", "GLE 450 AMG", "GLE 63 AMG", "GLE 63 S AMG", "GLE Coupe", "GLK", "GLS", "ML", "ML 230", "ML 250", "ML 270", "ML 280", "ML 300", "ML 320", "ML 350", "ML 400", "ML 420", "ML 430", "ML 450", "ML 500", "ML 55 AMG", "ML 63 AMG", "R", "R 280", "R 300", "R 320", "R 350", "R 500", "R 63 AMG", "S", "S 250", "S 280", "S 300", "S 320", "S 350", "S 400", "S 420", "S 430", "S 450", "S 500", "S 55 AMG", "S 550", "S 560", "S 600", "S 63", "S 63 AMG", "S 65", "S 65 AMG", "SL", "SL 55 AMG", "SL 60 AMG", "SL 63 AMG", "SL 65 AMG", "SLC", "SLK", "SLK 32 AMG", "SLK 55 AMG", "SLR", "SLS", "SLS AMG", "Vaneo", "X-Klasse"]
   "Mercury"       ["Marauder", "Milan", "Monarch", "Mountaineer", "Villager"]
   "Mg"            ["Mga", "Mgb", "Mgf", "Tf", "Zr", "Zs", "Zt", "Zt-t"]
   "Microcar"      ["MC1", "MC2", "MGO"]
   "Mini"          ["Clubman", "Cooper", "Cooper cabrio", "Cooper s", "Cooper s cabrio", "Countryman", "Coupe", "D one", "One", "One cabrio", "Paceman"]
   "Mitsubishi"    ["3000 gt", "ASX", "Carisma", "Colt", "Cordia", "Eclipse", "Galant", "Grandis", "I-MiEV", "L200", "Lancer", "Montero", "Outlander", "Pajero", "Pajero pinin", "Pajero sport", "Sapporo", "Sigma", "Space gear", "Space runner", "Space star", "Space wagon", "Starion", "Tredia"]
   "Morgan"        ["Aero8"]
   "Moskvich"      ["1360", "1361", "1500", "2136", "2138", "2140", "2141", "21412", "21417", "2142", "2715", "401", "403", "407", "408", "412", "426", "427", "503", "Aleko", "Иж"]
   "Nissan"        ["100 nx", "200 sx", "240 z", "280 z", "300 zx", "350z", "370Z", "Almera", "Almera tino", "Altima", "Armada", "Bluebird", "Cedric", "Cherry", "Cube", "Figaro", "Frontier", "Gt-r", "Juke", "Kubistar", "Laurel", "Leaf ", "Maxima", "Micra", "Murano", "NP300", "Navara", "Note", "Pathfinder", "Patrol", "Pickup", "Pixo", "Prairie", "Primera", "Pulsar", "Qashqai", "Rogue", "Sentra", "Serena", "Silvia", "Skyline", "Stantza", "Sunny", "Teana", "Terrano", "Tiida", "Titan crew cab", "Titan king", "Versa", "X-trail", "Xterra", "e-NV200"]
   "Oldsmobile"    ["Achieva", "Alero", "Aurora", "Bravada", "Cutlass", "Firenza", "Intrigue", "Regency", "Silhouette", "Toronado"]
   "Opel"          ["Adam", "Admiral", "Agila", "Ampera", "Antara", "Ascona", "Astra", "Calibra", "Campo", "Cascada", "Combo", "Commodore", "Corsa", "Crossland X", "Diplomat", "Frontera", "Grandland X", "Gt", "Insignia", "Kadett", "Kapitaen", "Karl", "Manta", "Meriva", "Mokka", "Monterey", "Monza", "Omega", "Rekord", "Senator", "Signum", "Sintra", "Speedster", "Tigra", "Vectra", "Zafira"]
   "Perodua"       ["Kancil", "Kelisa", "Kembara", "Kenari", "Nippa", "Rusa"]
   "Peugeot"       ["1007", "104", "106", "107", "108", "2008", "202", "204", "205", "206", "207", "208", "3008", "301", "304", "305", "306", "307", "308", "309", "4007", "4008", "402", "403", "404", "405", "406", "407", "5008", "504", "505", "508", "604", "605", "607", "806", "807", "Bipper", "Partner", "RCZ", "Range", "iOn"]
   "Pgo"           ["Cevennes", "Speedster"]
   "Plymouth"      ["Acclaim", "Barracuda", "Breeze", "Colt", "Grand voyager", "Horizon", "Laser", "Neon", "Prowler", "Reliant", "Road runner", "Sundance", "Volare", "Voyager"]
   "Polonez"       ["Pickup"]
   "Pontiac"       ["Aztec", "Bonneville", "Fiero", "Firebird", "G6", "Grand am", "Grand prix", "Gto", "Lemans", "Solstice", "Sunbird", "Sunfire", "Tempest", "Trans am", "Trans sport", "Vibe"]
   "Porsche"       ["356 Speedster", "911", "918 Spyder", "924", "928", "935", "944", "956", "968", "991", "993", "996", "Boxster", "Carrera", "Cayenne", "Cayman", "Macan", "Panamera"]
   "Proton"        ["400", "Arena", "Persone", "Satria"]
   "Renault"       ["10", "11", "12", "14", "16", "18", "19", "20", "21", "25", "29", "30", "4", "5", "8", "9", "Alpine", "Avantime", "Bakara", "Bulgar", "Captur", "Chamade", "Clio", "Espace", "Express", "Fluence", "Fuego", "Grand espace", "Grand scenic", "Kadjar", "Kangoo", "Koleos", "Laguna", "Laguna Coupe", "Latitude", "Megane", "Modus", "Nevada", "Rapid", "Safrane", "Scenic", "Scenic rx4", "Symbol", "Talisman", "Twingo", "Twizy", "Vel satis", "Wind", "Zoe"]
   "Rolls-Royce"   ["Ghost", "Phantom", "Silver Seraph", "Wraith"]
   "Rover"         ["111", "114", "200", "213", "214", "216", "220", "25", "400", "414", "416", "418", "420", "45", "600", "618", "620", "623", "75", "800", "820", "825", "827", "City", "Estate", "Maestro", "Metro", "Mini", "Montego", "Streetwise"]
   "SECMA"         ["F16", "F440DCI", "Fun Buggy", "Fun Extreem", "Fun Lander", "Fun Quad"]
   "SH auto"       ["Ceo"]
   "Saab"          ["9-3", "9-4X", "9-5", "9-7x", "900", "9000"]
   "Samand"        ["LX"]
   "Saturn"        ["Astra", "Aura", "Outlook", "Sky", "Vue"]
   "Scion"         ["Tc", "Xa", "Xb"]
   "Seat"          ["Alhambra", "Altea", "Arona", "Arosa", "Ateca", "Cordoba", "Exeo", "Fura", "Ibiza", "Inka", "Leon", "Malaga", "Marbella", "Mii", "Ronda", "Terra", "Toledo", "Vario"]
   "Shatenet"      ["Stella"]
   "Shuanghuan"    ["Ceo", "Noble"]
   "Simca"         ["1307", "1308", "1309", "1510", "Aront", "Chrysler", "Horizon", "Shambord", "Solara", "Special", "Versail"]
   "Skoda"         ["100", "1000", "105", "120", "125", "130", "135", "136", "Citigo", "Fabia", "Favorit", "Felicia", "Forman", "Karoq", "Kodiaq", "Octavia", "Praktik", "Rapid", "Roomster", "Superb", "Yeti"]
   "Smart"         ["Forfour", "Fortwo", "Mc", "Roadster"]
   "Ssang yong"    ["Chairman", "Rodius"]
   "SsangYong"     ["Actyon", "Actyon Sports", "Korando", "Korando Sports", "Kyron", "Musso", "Rexton", "Tivoli", "XLV"]
   "Subaru"        ["1800", "B9 tribeca", "BRZ", "Baja", "E12", "Forester", "G3x justy", "Impreza", "Justy", "Legacy", "Levorg", "Libero", "Outback", "Svx", "Trezia", "Vivio", "XT", "XV"]
   "Suzuki"        ["Alto", "Baleno", "Celerio", "Forenza", "Grand vitara", "Ignis", "Jimny", "Kizashi", "Liana", "Maruti", "Reno", "SX4", "SX4 S-Cross", "Samurai", "Santana", "Sg", "Sidekick", "Sj", "Splash", "Swift", "Vitara", "Wagon r", "X-90", "XL-7"]
   "Talbot"        ["1100", "1310", "Horizon", "Matra", "Murena", "Samba", "Simka", "Solara"]
   "Tata"          ["Aria", "Estate", "Indica", "Mint", "Nano", "Safari", "Sierra", "Sumo", "Telcoline", "Xenon"]
   "Tavria"        [".", "Dana", "Kombi", "Slavuta"]
   "Tazzari"       ["Zero"]
   "Tempo"         ["Gurkha", "Judo"]
   "Terberg"       ["Fl2850", "Sl3000"]
   "Tesla"         ["Model 3", "Model S", "Model X", "Roadster", "Roadster Sport"]
   "Tofas"         ["Dogan", "Kartal", "Sahin"]
   "Toyota"        ["4runner", "Auris", "Avalon", "Avensis", "Avensis verso", "Aygo", "C-HR", "Camry", "Carina", "Celica", "Corolla", "Corolla Matrix", "Corolla verso", "Cressida", "Crown", "Fj cruiser", "GT86", "Harrier", "Highlander", "Hilux", "IQ", "Land cruiser", "Matrix", "Mr2", "Paseo", "Picnic", "Previa", "Prius", "Rav4", "Scion", "Sequoia", "Sienna", "Starlet", "Supra", "Tacoma", "Tercel", "Tundra", "Urban Cruiser", "Venza", "Verso", "Verso S", "Yaris", "Yaris verso"]
   "Trabant"       ["600", "601", "Combi", "T 1.1"]
   "Triumph"       ["Acclaim", "Dolomite", "Herald", "Spitfire", "Stag", "Tr6", "Tr7"]
   "Uaz"           ["2206", "23602 Cargo", "3303", "3741", "390945", "390995", "452", "460", "469", "669", "69", "Hunter", "Patriot", "Профи"]
   "VROMOS"        ["Kiwi", "Rhea"]
   "VW"            ["1200", "1300", "1302", "1303", "1500", "1600", "Alltrack", "Amarok", "Arteon", "Atlas", "Bora", "CC", "Caddy", "Corrado", "Derby", "Eos", "Fox", "Golf", "Golf Plus", "Golf Variant", "Jetta", "K 70", "Karmann-ghia", "Lupo", "Multivan", "New beetle", "Passat", "Passat CC", "Phaeton", "Polo", "Rabbit", "Santana", "Scirocco", "Sharan", "T-Roc", "Taro", "Tiguan", "Touareg", "Touran", "Up", "Vento"]
   "Volga"         ["22", "24", "3110", "3111", "M 20", "M 21", "Siber"]
   "Volvo"         ["142", "144", "145", "164", "1800 es", "240", "244", "245", "262 c", "264", "340", "343", "344", "345", "360", "440", "460", "480", "66", "740", "744", "745", "760", "765", "770", "780", "850", "940", "960", "C30", "C70", "P 1800", "S40", "S60", "S70", "S80", "S90", "V40", "V50", "V60", "V70", "V90", "V90 Cross Country", "XC40", "XC60", "Xc70", "Xc90"]
   "Warszawa"      ["223", "232"]
   "Wartburg"      ["1.3", "311", "312", "353"]
   "Wiesmann"      ["Gt", "Mf3", "Mf4", "Mf5"]
   "Xinkai"        ["1021d", "1021ls", "1021s", "2021d", "2021s"]
   "Xinshun"       ["XS-D055"]
   "Zastava"       ["600", "750", "Florida", "Gt 55", "Koral", "Miami", "Yugo 45"]
   "Zaz"           ["1102", "1103", "1105", "965", "966", "968", "Tavria"]
   "Други"         [" "]
   "Победа"        ["М"]
   "София"         ["С"]
   "Чайка"         ["М"]
   })

(def ^:dynamic *base-url* "https://www.auto.bg/pcgi/results.cgi?cat=1&marka=%s&model=%s&page=%s")

(def ^:dynamic *cars-ads-selector*
  [html/root :#rightColumn :> :div.results :> :.resultItem :> :ul :> :li])

(def ^:dynamic *car-headline-selector*
  [html/root :> :.head])

(def ^:dynamic *encoding* "windows-1251")

(def ^:dynamic *car-description-selector* [html/root :> :.info :> :div])

(def ^:dynamic *car-summary-selector* [html/root :> :.info :> :span])

(defn split-on-space [word]
  "Splits a string on words"
  (clojure.string/split word #"\s+"))

(defn squish [line]
  (str/triml (str/join " "
                       (split-on-space (str/replace line #"\n" " ")))))

(defn fetch-url [url encoding]
  (-> url java.net.URL.
      .getContent (java.io.InputStreamReader. encoding)
      html/html-resource))

(defn cars [make model page]
  (let [url (format *base-url* make model page)]
    (html/select (fetch-url url *encoding*) *cars-ads-selector*)))

(defn tokenize [car-info]
  (if (nil? car-info)
    nil
    (zipmap [:first-registered
             :odometer-reading
             :gearbox-type
             :fuel-type
             :power] (map str/trim (str/split car-info #"\|")))))

(defn extract [node]
  (let [car-headline (first (html/select [node] *car-headline-selector*))
        car-info (tokenize (html/text (first (html/select [node] *car-description-selector*))))
        car-summary (first (html/select [node] *car-summary-selector*))
        result (vec (conj (map squish (map html/text [car-headline car-summary])) car-info))]
    (zipmap [:headline :info :summary] result)))

(defn empty-car-listing? [node]
  (some (fn [[_ v]] (= v "")) node))

(defn check [story key default]
  (let [v (key story)]
    (if (not= v "") v default)))

(defn print-car [story]
  (println)
  (println (check story :headline "No headline"))
  (println "\t" (check story :info "No info"))
  (println "\t" (check story :summary "No summary")))

(defn print-cars []
  (doseq [car (remove empty-car-listing? (map extract (cars "Ford" "Mondeo" 3)))]
    (print-car car)))