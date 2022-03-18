import request from '@/util/request'

export function query(params) {
  return request({
    url: '/testrecord/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(params) {
  return request({
    url: '/testrecord/queryByProjectId',
    method: 'get',
    params: params
  })
}
export function getById(id) {
  return request({
    url: '/testrecord/' + id,
    method: 'get'
  })
}