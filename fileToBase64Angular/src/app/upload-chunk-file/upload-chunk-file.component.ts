import { Component, OnInit } from '@angular/core';
import { NzMessageService, UploadFile } from 'ng-zorro-antd';
import { of, pipe, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UploadFileService } from '../services/upload-file.service';

@Component({
  selector: 'app-upload-chunk-file',
  templateUrl: './upload-chunk-file.component.html',
  styleUrls: ['./upload-chunk-file.component.scss']
})
export class UploadChunkFileComponent implements OnInit {

  public fileList: UploadFile[] = [];
  public uploading = false;
  private chunkSize = 100 * 1000; //Tamaño en bytes, aqui es un Mb
  private currentIndex = 0;
  private base64 = "";
  private totalChunkIndex = 0;

  constructor(private upload: UploadFileService) { }

  ngOnInit(): void {
  }


  public beforeUpload = (file: UploadFile): boolean => {
    this.fileList = this.fileList.concat(file);
    return false;
  };


  public loadFile(): void {
    const file = this.fileList[0];
    const totalChunks = Math.ceil(file.size / this.chunkSize) - 1;
    console.log("file size: ", file.size);

    const reader = new FileReader();
    let from = this.currentIndex * this.chunkSize;
    let to = from + this.chunkSize;
    let blob: Blob = new Blob();
    if (this.currentIndex === totalChunks) {
      blob = file.slice((from - this.chunkSize), file.size);
    } else {
      blob = file.slice(from, to);
    }
    console.log("current", this.currentIndex);

    reader.readAsDataURL(blob);
    reader.onload = (readerEvent: ProgressEvent<FileReader>) => {
      const data = readerEvent.target.result;
      this.base64 += data;
      if (this.currentIndex <= totalChunks) {
        this.currentIndex++;
        this.loadFile();
      } else {
        console.log("complete", { base: this.base64 });
        console.log("complete", this.base64.length);
        this.currentIndex = 0;
      }
      // this.upload.uploadChunkFile(this.fileList[0],
      //   this.currentIndex.toString(),
      //   this.chunkSize,
      //   data)
      //   .subscribe(response => {
      //     console.log("response", response);
      //     if (this.currentIndex <= totalChunks) {
      //       this.currentIndex = response;
      //       this.loadFile();
      //     } else {
      //       console.log("complete");
      //       this.currentIndex = 0;
      //     }
      //   },
      //     error => {
      //       console.log("error");
      //       this.currentIndex = 0;
      //     });
    };



  }
  public loadFile2(): void {
    const file: any = this.fileList[0];
    console.log("file size: ", file.size);

    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (readerEvent: ProgressEvent<FileReader>) => {
      // 1 507 436
      const data = readerEvent.target.result.toString().split(",")[1];
      // 100000 = 100kb || 1000000 = 1 mb
      console.log("data.length", data.length);

      this.totalChunkIndex = Math.ceil(data.length / this.chunkSize);
      // console.log("total", Math.ceil(data.length / this.chunkSize));
      // console.log("data: ", { data: data.toString().slice(0, this.chunkSize) });

      const chunkData = data.slice(this.currentIndex * this.chunkSize, ((this.currentIndex + 1) * this.chunkSize));
      this.upload.uploadChunkFile(
        this.fileList[0],
        (this.currentIndex + 1).toString(),
        this.totalChunkIndex,
        this.chunkSize,
        chunkData
      )
        .subscribe(response => {
          console.log("response: ", response);
          console.log("current: ", this.currentIndex);
          if (this.currentIndex < this.totalChunkIndex) {
            this.currentIndex++;
            this.loadFile2();
          } else {
            this.currentIndex = 0;
            this.totalChunkIndex = 0;
          }

        },
          error => {
            console.log(error);

            this.currentIndex = 0;
            this.totalChunkIndex = 0;
          })
        ;

    };



  }
}

