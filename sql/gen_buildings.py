"""生成 01-05 号楼 room/bed 演示 SQL"""
floors = ['一层', '二层', '三层', '四层', '五层']
floor_prefix = ['1', '2', '3', '4', '5']
buildings = ['01', '02', '03', '04', '05']
room_id = 51
bed_id = 201
lines = [
    '-- demo-data-buildings-01-05.sql',
    '-- 01-05 号楼房间与床位演示数据（在 demo-data.sql 之后执行）',
    '',
    'USE elder_care_system;',
    '',
]
for building in buildings:
    lines.append(f'-- ---------- {building} 号楼 ----------')
    for floor, fp in zip(floors, floor_prefix):
        for ri in range(1, 3):
            room_no = fp + '00' + str(ri)
            lines.append(
                f"INSERT INTO `room` VALUES ({room_id}, '{room_no}', '{floor}', '{building}', '0', NULL, NULL, NULL, NULL);"
            )
            for b in range(1, 5):
                lines.append(
                    f"INSERT INTO `bed` VALUES ({bed_id}, '{b}', 'free', {room_id}, '0', NULL, NULL, NULL, NULL);"
                )
                bed_id += 1
            room_id += 1
    lines.append('')

out = __file__.replace('gen_buildings.py', 'demo-data-buildings-01-05.sql')
with open(out, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))
print(f'Wrote {out}, rooms 51-{room_id - 1}, beds 201-{bed_id - 1}')
