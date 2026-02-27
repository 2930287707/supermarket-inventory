import request from '@/utils/request'

/**
 * 库存操作（入库/出库）
 * @param data { goodId, type, qty, remark, operatorId }
 */
export function operateStock(data) {
  return request({
    url: '/stock/operate',
    method: 'post',
    data
  })
}

/**
 * 获取库存流水记录列表
 * 注意：导出的名称必须与页面中 import 的名称完全一致
 */
export function getStockList() {
  return request({
    url: '/stock/list',
    method: 'get'
  })
}
