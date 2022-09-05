package com.caston.netdisc.service.impl;

import com.caston.netdisc.entity.File;
import com.caston.netdisc.mapper.FileMapper;
import com.caston.netdisc.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-09-02
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

}
