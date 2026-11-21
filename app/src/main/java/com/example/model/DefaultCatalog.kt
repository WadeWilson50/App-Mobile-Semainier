package com.example.model

object DefaultCatalog {

    val DEFAULT_RECIPES: List<Recipe> = listOf(
        // --- AIRFRYER (6 recettes) ---
        Recipe(
            id = "af-1",
            name = "Croque-monsieur croustillants à l’Airfryer",
            appliance = "Airfryer",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 10,
            notes = "Tartiner le pain de mie de crème fraîche, garnir de jambon et emmental râpé. Cuire à l’Airfryer à 180°C pendant 7 à 8 min jusqu’à ce que ce soit bien doré. Servir avec une salade verte.",
            ingredients = listOf(
                Ingredient("Pain de mie (tranches)", 2.0, "tranches", "Boulangerie"),
                Ingredient("Jambon blanc", 1.0, "tranche", "Boucherie & Poissonnerie"),
                Ingredient("Emmental râpé", 40.0, "g", "Crèmerie & Œufs"),
                Ingredient("Crème fraîche épaisse", 1.0, "c. à soupe", "Crèmerie & Œufs"),
                Ingredient("Salade verte", 0.25, "pièce", "Fruits & Légumes")
            ),
            isFavorite = true
        ),
        Recipe(
            id = "af-2",
            name = "Pilons de poulet rôtis & pommes grenailles",
            appliance = "Airfryer",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 20,
            notes = "Mélanger pilons de poulet et pommes de terre grenailles coupées en deux avec 1 c. à soupe d’huile d’olive, paprika et ail en poudre. Cuire à 190°C pendant 18-20 min en secouant à mi-cuisson.",
            ingredients = listOf(
                Ingredient("Pilons de poulet", 2.0, "pièces", "Boucherie & Poissonnerie"),
                Ingredient("Pommes de terre grenailles", 180.0, "g", "Fruits & Légumes"),
                Ingredient("Paprika doux", 0.5, "c. à café", "Épicerie")
            )
        ),
        Recipe(
            id = "af-3",
            name = "Pavés de saumon & légumes rôtis express",
            appliance = "Airfryer",
            category = "Traditionnel",
            moment = "any",
            isFish = true,
            time = 15,
            notes = "Disposer les fleurettes de brocoli ou courgettes en dés arrosées d’huile d’olive dans le panier (5 min à 180°C), puis ajouter les pavés de saumon assaisonnés pour 9 min à 180°C.",
            ingredients = listOf(
                Ingredient("Pavé de saumon", 1.0, "pièce", "Boucherie & Poissonnerie"),
                Ingredient("Courgette ou brocoli", 150.0, "g", "Fruits & Légumes"),
                Ingredient("Citron jaune", 0.25, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "af-4",
            name = "Feuilletés jambon-fromage express",
            appliance = "Airfryer",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 12,
            notes = "Couper la pâte feuilletée en carrés, garnir de jambon et fromage râpé, replier en chaussons et dorer au jaune d’œuf ou lait. Airfryer 180°C pendant 10-12 min.",
            ingredients = listOf(
                Ingredient("Pâte feuilletée prête à dérouler", 0.35, "rouleau", "Crèmerie & Œufs"),
                Ingredient("Jambon blanc", 1.0, "tranche", "Boucherie & Poissonnerie"),
                Ingredient("Emmental râpé", 35.0, "g", "Crèmerie & Œufs"),
                Ingredient("Salade verte", 0.25, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "af-5",
            name = "Nuggets maison croustillants & frites de patates douces",
            appliance = "Airfryer",
            category = "Convivial",
            moment = "midi",
            isFish = false,
            time = 20,
            notes = "Couper les patates douces en frites et les aiguillettes de poulet panées dans de la chapelure (avec un peu d’œuf). Cuire à 190°C pendant 15-18 min en secouant à mi-cuisson.",
            ingredients = listOf(
                Ingredient("Aiguillettes de poulet", 120.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Patate douce", 160.0, "g", "Fruits & Légumes"),
                Ingredient("Chapelure dorée", 25.0, "g", "Épicerie"),
                Ingredient("Œufs frais", 0.5, "pièce", "Crèmerie & Œufs")
            )
        ),
        Recipe(
            id = "af-6",
            name = "Camembert rôti & mouillettes de pain croustillant",
            appliance = "Airfryer",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 12,
            notes = "Quadriller le dessus du camembert remis dans sa boîte en bois (sans papier). Cuire 10 min à 180°C avec des mouillettes de baguette à côté. Servir avec jambon cru et salade.",
            ingredients = listOf(
                Ingredient("Camembert au lait pasteurisé", 0.35, "pièce", "Crèmerie & Œufs"),
                Ingredient("Baguette de tradition", 0.35, "pièce", "Boulangerie"),
                Ingredient("Jambon cru", 1.5, "tranches", "Boucherie & Poissonnerie"),
                Ingredient("Salade verte", 0.3, "pièce", "Fruits & Légumes")
            )
        ),

        // --- COOKEO (6 recettes) ---
        Recipe(
            id = "ck-1",
            name = "Sauté de porc aux carottes & moutarde",
            appliance = "Cookeo",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 15,
            notes = "Mode Dorer 3 min avec le sauté de porc et l’oignon. Ajouter les carottes en rondelles, 1 c. à soupe de moutarde et 100 ml d’eau. Cuisson sous pression : 10 min. Ajouter la crème à la fin.",
            ingredients = listOf(
                Ingredient("Sauté de porc", 140.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Carottes", 1.5, "pièces", "Fruits & Légumes"),
                Ingredient("Oignon jaune", 0.25, "pièce", "Fruits & Légumes"),
                Ingredient("Moutarde douce", 1.0, "c. à café", "Épicerie"),
                Ingredient("Crème fraîche épaisse", 25.0, "g", "Crèmerie & Œufs")
            ),
            isFavorite = true
        ),
        Recipe(
            id = "ck-2",
            name = "Risotto crémeux aux champignons et jambon",
            appliance = "Cookeo",
            category = "Traditionnel",
            moment = "any",
            isFish = false,
            time = 12,
            notes = "Mode Dorer 2 min : riz arborio + champignons émincés. Ajouter 2,5 fois le volume du riz en bouillon + dés de jambon. Cuisson sous pression : 9 min. Incorporer parmesan/emmental en fin de cuisson.",
            ingredients = listOf(
                Ingredient("Riz arborio (risotto)", 85.0, "g", "Épicerie"),
                Ingredient("Champignons de Paris frais", 60.0, "g", "Fruits & Légumes"),
                Ingredient("Dés de jambon", 50.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Parmesan ou emmental râpé", 25.0, "g", "Crèmerie & Œufs"),
                Ingredient("Cube de bouillon de volaille", 0.35, "cube", "Épicerie")
            )
        ),
        Recipe(
            id = "ck-3",
            name = "Poulet basquaise express au Cookeo",
            appliance = "Cookeo",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 15,
            notes = "Dorer les aiguillettes de poulet avec poivrons émincés et oignon pendant 3 min. Ajouter les tomates concassées et un peu d’eau. Cuisson sous pression : 8 min. Servir avec du riz.",
            ingredients = listOf(
                Ingredient("Aiguillettes de poulet", 130.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Poivron rouge ou vert", 0.5, "pièce", "Fruits & Légumes"),
                Ingredient("Tomates concassées (conserve)", 100.0, "g", "Épicerie"),
                Ingredient("Oignon jaune", 0.25, "pièce", "Fruits & Légumes"),
                Ingredient("Riz blanc ou basmati", 65.0, "g", "Épicerie")
            )
        ),
        Recipe(
            id = "ck-4",
            name = "Pâtes One-Pot carbonara express",
            appliance = "Cookeo",
            category = "Convivial",
            moment = "any",
            isFish = false,
            time = 10,
            notes = "Mettre les pâtes crues, les lardons, l’oignon émincé et couvrir tout juste d’eau (environ 100 ml par personne). Cuisson sous pression : moitié du temps indiqué sur le paquet de pâtes (4-5 min). Ajouter crème et fromage.",
            ingredients = listOf(
                Ingredient("Pâtes (penne ou coquillettes)", 95.0, "g", "Épicerie"),
                Ingredient("Lardons fumés", 50.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Crème liquide", 40.0, "ml", "Crèmerie & Œufs"),
                Ingredient("Emmental ou parmesan râpé", 25.0, "g", "Crèmerie & Œufs")
            )
        ),
        Recipe(
            id = "ck-5",
            name = "Blanquette de dinde express au Cookeo",
            appliance = "Cookeo",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 18,
            notes = "Dorer les morceaux de dinde 3 min. Ajouter carottes en rondelles, champignons et bouillon. Cuisson sous pression : 10 min. Lier avec crème fraîche et 1 c. à café de maïzena.",
            ingredients = listOf(
                Ingredient("Sauté de dinde", 140.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Carottes", 1.0, "pièce", "Fruits & Légumes"),
                Ingredient("Champignons de Paris frais", 50.0, "g", "Fruits & Légumes"),
                Ingredient("Crème fraîche épaisse", 35.0, "g", "Crèmerie & Œufs"),
                Ingredient("Riz blanc ou basmati", 65.0, "g", "Épicerie")
            )
        ),
        Recipe(
            id = "ck-6",
            name = "Chili con carne express familial",
            appliance = "Cookeo",
            category = "Convivial",
            moment = "any",
            isFish = false,
            time = 15,
            notes = "Dorer le bœuf haché et l’oignon 3 min avec épices chili/cumin. Ajouter haricots rouges égouttés, maïs, tomates concassées et un fond d’eau. Cuisson sous pression : 8 min.",
            ingredients = listOf(
                Ingredient("Bœuf haché 5%", 110.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Haricots rouges (conserve)", 80.0, "g", "Épicerie"),
                Ingredient("Tomates concassées (conserve)", 100.0, "g", "Épicerie"),
                Ingredient("Maïs doux (conserve)", 40.0, "g", "Épicerie"),
                Ingredient("Riz blanc ou basmati", 60.0, "g", "Épicerie")
            )
        ),

        // --- THERMOMIX TM6 (6 recettes) ---
        Recipe(
            id = "tm-1",
            name = "Velouté de légumes & tartines gratinées",
            appliance = "Thermomix TM6",
            category = "Traditionnel",
            moment = "soir",
            isFish = false,
            time = 20,
            notes = "Mettre légumes en morceaux + bouillon dans le bol : 18 min / 100°C / Vit 1, puis mixer 1 min / Vit 10. Servir avec des tartines de pain grillées au fromage.",
            ingredients = listOf(
                Ingredient("Potiron ou carottes/poireaux", 220.0, "g", "Fruits & Légumes"),
                Ingredient("Pommes de terre", 70.0, "g", "Fruits & Légumes"),
                Ingredient("Pain de campagne (tranches)", 1.5, "tranches", "Boulangerie"),
                Ingredient("Emmental râpé", 30.0, "g", "Crèmerie & Œufs"),
                Ingredient("Crème fraîche épaisse", 20.0, "g", "Crèmerie & Œufs")
            ),
            isFavorite = true
        ),
        Recipe(
            id = "tm-2",
            name = "Hachis Parmentier express au TM6",
            appliance = "Thermomix TM6",
            category = "Traditionnel",
            moment = "any",
            isFish = false,
            time = 25,
            notes = "Cuire les pommes de terre en dés dans le panier cuisson avec le lait/eau (18 min / Varoma / Vit 1) pendant que le bœuf haché revient à la poêle. Fouetter la purée 30 sec / Vit 4 et gratiner 5 min au four.",
            ingredients = listOf(
                Ingredient("Pommes de terre", 240.0, "g", "Fruits & Légumes"),
                Ingredient("Bœuf haché 5%", 115.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Lait demi-écrémé", 50.0, "ml", "Crèmerie & Œufs"),
                Ingredient("Emmental râpé", 25.0, "g", "Crèmerie & Œufs")
            )
        ),
        Recipe(
            id = "tm-3",
            name = "Quiche lorraine sans pâte express",
            appliance = "Thermomix TM6",
            category = "Traditionnel",
            moment = "soir",
            isFish = false,
            time = 25,
            notes = "Mélanger œufs, farine, lait, crème et muscade dans le bol : 20 sec / Vit 4. Verser dans un moule beurré sur les lardons et le fromage râpé. Cuire 20 min à 190°C.",
            ingredients = listOf(
                Ingredient("Œufs frais", 1.0, "pièce", "Crèmerie & Œufs"),
                Ingredient("Lardons fumés", 55.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Lait demi-écrémé", 80.0, "ml", "Crèmerie & Œufs"),
                Ingredient("Farine de blé", 25.0, "g", "Épicerie"),
                Ingredient("Emmental râpé", 30.0, "g", "Crèmerie & Œufs"),
                Ingredient("Salade verte", 0.25, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "tm-4",
            name = "Curry de poulet coco & riz vapeur Varoma",
            appliance = "Thermomix TM6",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 20,
            notes = "Mettre le riz et l’eau dans le panier cuisson, le poulet en dés + lait de coco + curry dans le bol (ou Varoma) : 18 min / Varoma / Sens inverse / Vit mijotage.",
            ingredients = listOf(
                Ingredient("Blanc de poulet", 130.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Lait de coco", 70.0, "ml", "Épicerie"),
                Ingredient("Riz blanc ou basmati", 70.0, "g", "Épicerie"),
                Ingredient("Curry doux en poudre", 0.5, "c. à café", "Épicerie")
            )
        ),
        Recipe(
            id = "tm-5",
            name = "Pizza maison express pâte TM6 & garniture",
            appliance = "Thermomix TM6",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 20,
            notes = "Pétrir farine, eau tiède, levure boulangère, huile d’olive et sel 2 min / Mode Pétrin. Étaler aussitôt, garnir de sauce tomate, jambon et mozzarella, enfourner 12 min à 220°C.",
            ingredients = listOf(
                Ingredient("Farine de blé", 90.0, "g", "Épicerie"),
                Ingredient("Levure boulangère sèche", 2.0, "g", "Épicerie"),
                Ingredient("Sauce tomate cuisinée", 50.0, "g", "Épicerie"),
                Ingredient("Mozzarella", 40.0, "g", "Crèmerie & Œufs"),
                Ingredient("Jambon blanc", 1.0, "tranche", "Boucherie & Poissonnerie")
            )
        ),
        Recipe(
            id = "tm-6",
            name = "Cabillaud vapeur Varoma & sauce citronnée",
            appliance = "Thermomix TM6",
            category = "Traditionnel",
            moment = "any",
            isFish = true,
            time = 18,
            notes = "Cuire les pommes de terre dans le panier et les dos de cabillaud dans le Varoma (16 min / Varoma / Vit 1). Émulsionner le jus de cuisson avec crème et citron 1 min / Vit 3.",
            ingredients = listOf(
                Ingredient("Dos de cabillaud", 140.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Pommes de terre", 180.0, "g", "Fruits & Légumes"),
                Ingredient("Crème fraîche épaisse", 25.0, "g", "Crèmerie & Œufs"),
                Ingredient("Citron jaune", 0.25, "pièce", "Fruits & Légumes")
            )
        ),

        // --- POÊLE & CASSEROLE (6 recettes) ---
        Recipe(
            id = "pc-1",
            name = "Omelette campagnarde lardons & pommes de terre",
            appliance = "Poêle & Casserole",
            category = "Traditionnel",
            moment = "soir",
            isFish = false,
            time = 15,
            notes = "Faire rissoler des petits dés de pommes de terre et les lardons à la poêle 10 min, verser les œufs battus et cuire 3-4 min selon la texture souhaitée. Servir avec de la salade.",
            ingredients = listOf(
                Ingredient("Œufs frais", 2.5, "pièces", "Crèmerie & Œufs"),
                Ingredient("Pommes de terre", 120.0, "g", "Fruits & Légumes"),
                Ingredient("Lardons fumés", 40.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Salade verte", 0.25, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "pc-2",
            name = "Spaghetti à la carbonara traditionnelle rapide",
            appliance = "Poêle & Casserole",
            category = "Traditionnel",
            moment = "any",
            isFish = false,
            time = 15,
            notes = "Cuire les spaghetti al dente. Faire dorer les lardons à la poêle. Hors du feu, mélanger les pâtes égouttées avec les jaunes d’œufs battus, le parmesan/emmental et un peu d’eau de cuisson.",
            ingredients = listOf(
                Ingredient("Spaghetti", 95.0, "g", "Épicerie"),
                Ingredient("Lardons fumés", 50.0, "g", "Boucherie & Poissonnerie"),
                Ingredient("Œufs frais", 1.0, "pièce", "Crèmerie & Œufs"),
                Ingredient("Parmesan ou emmental râpé", 25.0, "g", "Crèmerie & Œufs")
            ),
            isFavorite = true
        ),
        Recipe(
            id = "pc-3",
            name = "Steak haché façon bouchère & haricots verts persillés",
            appliance = "Poêle & Casserole",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 12,
            notes = "Cuire les steaks hachés à la poêle 2 à 3 min par face. Faire revenir les haricots verts (surgelés ou en bocal) avec une noisette de beurre et de l’ail.",
            ingredients = listOf(
                Ingredient("Steak haché 5%", 1.0, "pièce", "Boucherie & Poissonnerie"),
                Ingredient("Haricots verts", 150.0, "g", "Fruits & Légumes"),
                Ingredient("Beurre doux", 10.0, "g", "Crèmerie & Œufs")
            )
        ),
        Recipe(
            id = "pc-4",
            name = "Galettes complètes jambon, œuf & fromage",
            appliance = "Poêle & Casserole",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 12,
            notes = "Faire chauffer la galette de sarrasin dans une poêle beurrée, casser un œuf au centre, ajouter le jambon et l’emmental râpé, puis replier les bords dès que le blanc est pris.",
            ingredients = listOf(
                Ingredient("Galettes de sarrasin", 1.5, "pièces", "Crèmerie & Œufs"),
                Ingredient("Œufs frais", 1.0, "pièce", "Crèmerie & Œufs"),
                Ingredient("Jambon blanc", 1.0, "tranche", "Boucherie & Poissonnerie"),
                Ingredient("Emmental râpé", 30.0, "g", "Crèmerie & Œufs"),
                Ingredient("Salade verte", 0.25, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "pc-5",
            name = "Tartines chaudes chèvre-miel & noix au grill",
            appliance = "Poêle & Casserole",
            category = "Convivial",
            moment = "soir",
            isFish = false,
            time = 10,
            notes = "Dorer des tranches de pain de campagne, déposer des rondelles de bûche de chèvre, un filet de miel et quelques cerneaux de noix. Passer 5 min sous le grill ou à la poêle couverte.",
            ingredients = listOf(
                Ingredient("Pain de campagne (tranches)", 2.0, "tranches", "Boulangerie"),
                Ingredient("Bûche de fromage de chèvre", 60.0, "g", "Crèmerie & Œufs"),
                Ingredient("Miel liquide", 1.0, "c. à café", "Épicerie"),
                Ingredient("Salade verte", 0.3, "pièce", "Fruits & Légumes")
            )
        ),
        Recipe(
            id = "pc-6",
            name = "Saucisses de Toulouse & purée maison rapide",
            appliance = "Poêle & Casserole",
            category = "Traditionnel",
            moment = "midi",
            isFish = false,
            time = 20,
            notes = "Dorer les saucisses à la poêle 12-15 min à feu doux. En parallèle, cuire les pommes de terre coupées en petits dés dans l’eau bouillante salée (12 min) et écraser avec lait et beurre.",
            ingredients = listOf(
                Ingredient("Saucisses de Toulouse ou chipolatas", 1.5, "pièces", "Boucherie & Poissonnerie"),
                Ingredient("Pommes de terre", 220.0, "g", "Fruits & Légumes"),
                Ingredient("Lait demi-écrémé", 40.0, "ml", "Crèmerie & Œufs"),
                Ingredient("Beurre doux", 10.0, "g", "Crèmerie & Œufs")
            )
        )
    )

    fun buildDefaultEmptyPlan(): List<MealSlot> {
        val slots = mutableListOf<MealSlot>()
        SemainierConfig.DAYS.forEach { day ->
            SemainierConfig.MEAL_TYPES.forEach { meal ->
                val slotKey = "${day.key}_${meal.key}"
                slots.add(
                    MealSlot(
                        slotKey = slotKey,
                        dayKey = day.key,
                        mealKey = meal.key,
                        recipeId = null,
                        servings = day.defaultServings,
                        disabled = SemainierConfig.DEFAULT_DISABLED_SLOTS.contains(slotKey)
                    )
                )
            }
        }
        return slots
    }
}
