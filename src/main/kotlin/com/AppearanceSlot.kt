package com

enum class AppearanceSlot(val id: Int, val defaultMale: Int, val defaultFemale: Int) {
    GENDER(0, 0, 1),
    HEAD(1, 0, 48),
    CHEST(2, 18, 57),
    ARMS(3, 26, 65),
    HANDS(4, 35, 68),
    LEGS(5, 36, 77),
    FEET(6, 42, 80),
    BEARD(7, 16, 57),
    HAIR_COLOUR(8, 0, 0),
    CHEST_COLOUR(9, 0, 0),
    LEGS_COLOUR(10, 0, 0),
    FEET_COLOUR(11, 0, 0),
    SKIN_COLOUR(12, 0, 0)
    ;
}

enum class EquipmentSlotIds(val id: Int) {
    HEAD(0),
    CAPE(1),
    AMULET(2),
    WEAPON(3),
    BODY(4),
    SHIELD(5),
    LEG(7),
    HANDS(9),
    FEET(10),
    RING(12),
    AMMUNITION(13)
}