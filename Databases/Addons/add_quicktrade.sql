UPDATE `openrsc_npcdef` SET command = 'trade' WHERE id in (1, 48, 51, 55, 56, 58, 59, 69, 75, 82, 83, 84, 85, 87, 88, 101, 103, 105, 106, 112, 113, 115, 129, 130, 131, 141, 143, 145, 146, 149, 155, 156, 157, 165, 167, 168, 169, 173, 183, 185, 186, 187, 218, 220, 222, 223, 228, 230, 233, 235, 250, 260, 269, 278, 280, 282, 289, 297, 308, 325, 327, 328, 329, 330, 331, 336, 337, 371, 391, 501, 512, 514, 522, 528, 530, 532, 535, 537, 580, 581, 587, 616, 620, 661, 686, 687, 688, 689, 717, 719, 720, 773, 779, 780, 788, 793, 807);

UPDATE `openrsc_npcdef` SET command = 'teleport', command2 = 'trade' WHERE id = 54;
