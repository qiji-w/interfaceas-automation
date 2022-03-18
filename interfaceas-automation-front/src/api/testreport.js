import request from '@/util/request'

export function getById(id) {
  return request({
    url: '/testreport/' + id,
    method: 'get'
  })
}