package com.railse.workforcemgmt.mapper;

import com.railse.workforcemgmt.dto.AddCommentRequest;
import com.railse.workforcemgmt.dto.TaskCommentDto;
import com.railse.workforcemgmt.model.TaskComment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-02T15:14:07+0000",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TaskCommentMapperImpl implements TaskCommentMapper {

    @Override
    public TaskCommentDto toDto(TaskComment taskComment) {
        if ( taskComment == null ) {
            return null;
        }

        TaskCommentDto taskCommentDto = new TaskCommentDto();

        taskCommentDto.setAuthor( taskComment.getAuthor() );
        taskCommentDto.setComment( taskComment.getComment() );
        taskCommentDto.setId( taskComment.getId() );
        taskCommentDto.setTimestamp( taskComment.getTimestamp() );

        return taskCommentDto;
    }

    @Override
    public List<TaskCommentDto> toDtoList(List<TaskComment> commentList) {
        if ( commentList == null ) {
            return null;
        }

        List<TaskCommentDto> list = new ArrayList<TaskCommentDto>( commentList.size() );
        for ( TaskComment taskComment : commentList ) {
            list.add( toDto( taskComment ) );
        }

        return list;
    }

    @Override
    public TaskComment toEntity(AddCommentRequest addCommentRequest) {
        if ( addCommentRequest == null ) {
            return null;
        }

        TaskComment taskComment = new TaskComment();

        taskComment.setAuthor( addCommentRequest.getAuthor() );
        taskComment.setComment( addCommentRequest.getComment() );

        return taskComment;
    }
}
