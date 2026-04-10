import os

base = r'f:\Java\supermarket-inventory\src\main\java\com\supermarket\supermarketinventory'
ctrl_dir = os.path.join(base, 'controller')
common_dir = os.path.join(base, 'common')
os.makedirs(ctrl_dir, exist_ok=True)

controllers = [
    'CategoryController',
    'DashboardController',
    'GoodsController',
    'OperationLogController',
    'PurchaseOrderController',
    'StockController',
    'SupplierController',
    'UserController',
]

for c in controllers:
    src = os.path.join(common_dir, c + '.java')
    dst = os.path.join(ctrl_dir, c + '.java')
    with open(src, 'r', encoding='utf-8') as f:
        content = f.read()
    content = content.replace(
        'package com.supermarket.supermarketinventory.common;',
        'package com.supermarket.supermarketinventory.controller;'
    )
    imports_to_add = []
    if 'Result' in content and 'import com.supermarket.supermarketinventory.common.Result;' not in content:
        imports_to_add.append('import com.supermarket.supermarketinventory.common.Result;')
    if 'PageResult' in content and 'import com.supermarket.supermarketinventory.common.PageResult;' not in content:
        imports_to_add.append('import com.supermarket.supermarketinventory.common.PageResult;')
    if 'BusinessException' in content and 'import com.supermarket.supermarketinventory.common.BusinessException;' not in content:
        imports_to_add.append('import com.supermarket.supermarketinventory.common.BusinessException;')
    if 'ErrorCode' in content and 'import com.supermarket.supermarketinventory.common.ErrorCode;' not in content:
        imports_to_add.append('import com.supermarket.supermarketinventory.common.ErrorCode;')
    if imports_to_add:
        import_block = '\n'.join(imports_to_add)
        content = content.replace(
            'package com.supermarket.supermarketinventory.controller;',
            'package com.supermarket.supermarketinventory.controller;\n\n' + import_block
        )
    with open(dst, 'w', encoding='utf-8') as f:
        f.write(content)
    print('Created: ' + c)

print('All controllers moved.')
